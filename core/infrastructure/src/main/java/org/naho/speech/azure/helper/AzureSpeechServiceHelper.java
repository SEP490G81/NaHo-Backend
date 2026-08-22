package org.naho.speech.azure.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.cognitiveservices.speech.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.i18n.message.speech.SpeechDetailMessageKey;
import org.naho.shared.exception.InfrastructureException;
import org.naho.speech.azure.constant.AzurePronunciationScoreKey;
import org.naho.speech.azure.exception.AzureSpeechErrorCode;
import org.naho.speech.azure.model.SpeechAssessment;
import org.naho.speech.azure.model.WordAssessment;
import org.naho.speech.azure.type.SpeechAssessmentErrorType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AzureSpeechServiceHelper {
    private static final long EXTRA_PROCESSING_TIMEOUT_SECONDS = 30L;
    private static final long MAX_PROCESSING_TIMEOUT_SECONDS = 180L;
    private final ObjectMapper objectMapper;

    public long calculateProcessingTimeoutSeconds(double durationSeconds) {
        if (durationSeconds <= 0 || Double.isNaN(durationSeconds) || Double.isInfinite(durationSeconds)) {
            return 60L;
        }
        return Math.min(
                (long) Math.ceil(durationSeconds) + EXTRA_PROCESSING_TIMEOUT_SECONDS,
                MAX_PROCESSING_TIMEOUT_SECONDS);
    }

    public PronunciationAssessmentConfig createPronunciationAssessmentConfig(String referenceText) {
        String normalizedReferenceText = referenceText == null ? "" : referenceText.trim();
        boolean miscueEnabled = !normalizedReferenceText.isEmpty();

        PronunciationAssessmentConfig config = new PronunciationAssessmentConfig(
                normalizedReferenceText,
                PronunciationAssessmentGradingSystem.HundredMark,
                PronunciationAssessmentGranularity.Word,
                miscueEnabled);

        config.enableProsodyAssessment();
        return config;
    }

    public SpeechAssessment processResult(SpeechRecognitionResult result) {
        if (result.getReason() == ResultReason.RecognizedSpeech) {
            String jsonResult = result.getProperties().getProperty(PropertyId.SpeechServiceResponse_JsonResult);

            try {
                JsonNode root = objectMapper.readTree(jsonResult);
                JsonNode nBestNode = root.path(AzurePronunciationScoreKey.N_BEST).get(0);
                if (nBestNode == null) {
                    throw new InfrastructureException(
                            AzureSpeechErrorCode.SPEECH_AZURE_SERVICE_ERROR,
                            SpeechDetailMessageKey.SPEECH_AZURE_N_BEST_NODE_NULL);
                }

                String displayResultText = nBestNode.path(AzurePronunciationScoreKey.DISPLAY).asText();
                JsonNode pronNode = nBestNode.path(AzurePronunciationScoreKey.PRONUNCIATION_ASSESSMENT);

                double accuracyScore = pronNode.path(AzurePronunciationScoreKey.ACCURACY_SCORE).asDouble(0.0) / 10.0;
                double fluencyScore = pronNode.path(AzurePronunciationScoreKey.FLUENCY_SCORE).asDouble(0.0) / 10.0;
                double completenessScore = pronNode.path(AzurePronunciationScoreKey.COMPLETENESS_SCORE).asDouble(0.0) / 10.0;
                double pronScore = pronNode.path(AzurePronunciationScoreKey.PRON_SCORE).asDouble(0.0) / 10.0;

                double averageScore = (accuracyScore + fluencyScore + completenessScore + pronScore) / 4.0;

                List<WordAssessment> wordList = new ArrayList<>();
                JsonNode wordsNode = nBestNode.path(AzurePronunciationScoreKey.WORDS);
                if (wordsNode.isArray()) {
                    for (JsonNode wordNode : wordsNode) {
                        String wordStr = wordNode.path(AzurePronunciationScoreKey.WORD).asText();
                        JsonNode wordPronNode = wordNode.path(AzurePronunciationScoreKey.PRONUNCIATION_ASSESSMENT);
                        double wordAccuracy = wordPronNode.path(AzurePronunciationScoreKey.ACCURACY_SCORE)
                                .asDouble(0.0) / 10.0;
                        String errorType = wordPronNode.path(AzurePronunciationScoreKey.ERROR_TYPE)
                                .asText(AzurePronunciationScoreKey.NONE).toUpperCase();

                        wordList.add(WordAssessment.builder()
                                .word(wordStr)
                                .accuracyScore(wordAccuracy)
                                .errorType(SpeechAssessmentErrorType.valueOf(errorType))
                                .build());
                    }
                }

                return SpeechAssessment.builder()
                        .transcriptText(displayResultText)
                        .accuracyScore(accuracyScore)
                        .fluencyScore(fluencyScore)
                        .completenessScore(completenessScore)
                        .pronunciationScore(pronScore)
                        .averageScore(averageScore)
                        .words(wordList)
                        .build();

            } catch (IOException e) {
                PronunciationAssessmentResult sdkResult = PronunciationAssessmentResult.fromResult(result);
                return SpeechAssessment.builder()
                        .transcriptText(result.getText())
                        .accuracyScore(sdkResult.getAccuracyScore())
                        .fluencyScore(sdkResult.getFluencyScore())
                        .completenessScore(sdkResult.getCompletenessScore())
                        .pronunciationScore(sdkResult.getPronunciationScore())
                        .words(List.of())
                        .build();
            }
        } else if (result.getReason() == ResultReason.NoMatch) {
            throw new InfrastructureException(
                    AzureSpeechErrorCode.SPEECH_AZURE_SERVICE_ERROR,
                    SpeechDetailMessageKey.SPEECH_RECOGNITION_NO_MATCH);
        } else if (result.getReason() == ResultReason.Canceled) {
            CancellationDetails cancellation = CancellationDetails.fromResult(result);
            throw new InfrastructureException(
                    AzureSpeechErrorCode.SPEECH_AZURE_SERVICE_ERROR,
                    SpeechDetailMessageKey.SPEECH_RECOGNITION_CANCELLED,
                    cancellation.getErrorDetails());
        } else {
            throw new InfrastructureException(
                    AzureSpeechErrorCode.SPEECH_AZURE_SERVICE_ERROR,
                    SpeechDetailMessageKey.SPEECH_AZURE_SERVICE_UNKNOWN_ERROR_OCCUR);
        }
    }

    public SpeechAssessment mergeAssessments(List<SpeechAssessment> assessments) {
        if (assessments.isEmpty()) {
            return null;
        }
        if (assessments.size() == 1) {
            return assessments.getFirst();
        }

        StringBuilder fullTranscript = new StringBuilder();
        List<WordAssessment> allWords = new ArrayList<>();

        double totalAccuracy = 0.0;
        double totalFluency = 0.0;
        double totalCompleteness = 0.0;
        double totalPronScore = 0.0;
        double totalAverageScore = 0.0;
        int totalWordCount = 0;

        for (SpeechAssessment segment : assessments) {
            if (segment.getTranscriptText() != null && !segment.getTranscriptText().isEmpty()) {
                if (!fullTranscript.isEmpty()) {
                    fullTranscript.append(" ");
                }
                fullTranscript.append(segment.getTranscriptText());
            }

            if (segment.getWords() != null) {
                allWords.addAll(segment.getWords());
            }

            int wordCount = segment.getWords() != null ? segment.getWords().size() : 0;
            if (wordCount > 0) {
                totalAccuracy += segment.getAccuracyScore() * wordCount;
                totalFluency += segment.getFluencyScore() * wordCount;
                totalCompleteness += segment.getCompletenessScore() * wordCount;
                totalPronScore += segment.getPronunciationScore() * wordCount;
                totalAverageScore += segment.getAverageScore() * wordCount;
                totalWordCount += wordCount;
            } else {
                totalAccuracy += segment.getAccuracyScore();
                totalFluency += segment.getFluencyScore();
                totalCompleteness += segment.getCompletenessScore();
                totalPronScore += segment.getPronunciationScore();
                totalAverageScore += segment.getAverageScore();
                totalWordCount += 1;
            }
        }

        double finalAccuracy = (totalWordCount > 0 ? totalAccuracy / totalWordCount : 0.0);
        double finalFluency = (totalWordCount > 0 ? totalFluency / totalWordCount : 0.0);
        double finalCompleteness = (totalWordCount > 0 ? totalCompleteness / totalWordCount : 0.0);
        double finalPronScore = (totalWordCount > 0 ? totalPronScore / totalWordCount : 0.0);

        double finalAverageScore = (totalWordCount > 0 ? totalAverageScore / totalWordCount : 0.0);

        return SpeechAssessment.builder()
                .transcriptText(fullTranscript.toString())
                .accuracyScore(finalAccuracy)
                .fluencyScore(finalFluency)
                .completenessScore(finalCompleteness)
                .pronunciationScore(finalPronScore)
                .averageScore(finalAverageScore)
                .words(allWords)
                .build();
    }
}
