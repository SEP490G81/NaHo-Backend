package org.naho.speech.azure.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.cognitiveservices.speech.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.shared.constant.FileExtension;
import org.naho.shared.exception.InfrastructureException;
import org.naho.speech.azure.constant.AzurePronunciationScoreKey;
import org.naho.speech.azure.constant.AzureSpeechApplicationMessageKey;
import org.naho.speech.azure.exception.AzureSpeechApplicationErrorCode;
import org.naho.speech.model.SpeechAssessment;
import org.naho.speech.model.WordAssessment;
import org.naho.speech.type.SpeechAssessmentErrorType;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AzureSpeechServiceHelper {
    private final ObjectMapper objectMapper;

    private static final String TEMP_AUDIO_FILE_PREFIX = "audio_";

    /**
     * Tạo file audio tạm thời từ dữ liệu byte[] upload lên.
     * File sẽ được lưu trong thư mục temp của hệ điều hành
     * và dùng cho quá trình speech processing / pronunciation assessment.
     *
     * @param audioBytes dữ liệu audio dạng byte[]
     * @return file audio tạm thời
     */
    public File createTempAudioFile(byte[] audioBytes) {
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));

        try {
            Path tempFile = Files.createTempFile(
                    tempDir,
                    TEMP_AUDIO_FILE_PREFIX,
                    FileExtension.WAV_EXTENSION
            );

            Files.write(tempFile, audioBytes);

            return tempFile.toFile();

        } catch (IOException e) {
            throw new InfrastructureException(
                    AzureSpeechApplicationErrorCode.SPEECH_AUDIO_NOT_VALID,
                    AzureSpeechApplicationMessageKey.SPEECH_AUDIO_FILE_EMPTY
            );
        }
    }

    /**
     * Tạo cấu hình Pronunciation Assessment cho Azure Speech SDK.
     *
     * @param referenceText đoạn text chuẩn để đối chiếu phát âm
     * @return cấu hình Pronunciation Assessment
     */
    public PronunciationAssessmentConfig createPronunciationAssessmentConfig(String referenceText) {
        String normalizedReferenceText = referenceText == null ? "" : referenceText.trim();

        // Miscue detection chỉ hoạt động ổn định khi có reference text (scripted mode).
        // Với unscripted mode, Azure có thể trả về kết quả không chính xác hoặc inconsistent.
        boolean miscueEnabled = !normalizedReferenceText.isEmpty();

        PronunciationAssessmentConfig config = new PronunciationAssessmentConfig(
                normalizedReferenceText,
                PronunciationAssessmentGradingSystem.HundredMark,
                PronunciationAssessmentGranularity.Phoneme,
                miscueEnabled
        );

        // Bật thêm tính năng prosody (âm điệu, ngắt nghỉ)
        config.enableProsodyAssessment();

        return config;
    }

    public SpeechAssessment processResult(SpeechRecognitionResult result) {
        if (result.getReason() == ResultReason.RecognizedSpeech) {
            // Lấy kết quả thô dạng JSON để parsing chi tiết đầy đủ metrics
            String jsonResult = result.getProperties().getProperty(PropertyId.SpeechServiceResponse_JsonResult);

            try {
                JsonNode root = objectMapper.readTree(jsonResult);
                JsonNode nBestNode = root.path(AzurePronunciationScoreKey.N_BEST).get(0);
                if (nBestNode == null) {
                    throw new InfrastructureException(
                            AzureSpeechApplicationErrorCode.SPEECH_AZURE_SERVICE_ERROR,
                            AzureSpeechApplicationMessageKey.SPEECH_AZURE_N_BEST_NODE_NULL
                    );
                }

                String displayResultText = nBestNode.path(AzurePronunciationScoreKey.DISPLAY).asText();
                JsonNode pronNode = nBestNode.path(AzurePronunciationScoreKey.PRONUNCIATION_ASSESSMENT);

                double accuracyScore = pronNode.path(AzurePronunciationScoreKey.ACCURACY_SCORE).asDouble(0.0);
                double fluencyScore = pronNode.path(AzurePronunciationScoreKey.FLUENCY_SCORE).asDouble(0.0);
                double completenessScore = pronNode.path(AzurePronunciationScoreKey.COMPLETENESS_SCORE).asDouble(0.0);
                double pronScore = pronNode.path(AzurePronunciationScoreKey.PRON_SCORE).asDouble(0.0);

                // Lấy chi tiết từ (Word level)
                List<WordAssessment> wordList = new ArrayList<>();
                JsonNode wordsNode = nBestNode.path(AzurePronunciationScoreKey.WORDS);
                if (wordsNode.isArray()) {
                    for (JsonNode wordNode : wordsNode) {
                        String wordStr = wordNode.path(AzurePronunciationScoreKey.WORD).asText();
                        JsonNode wordPronNode = wordNode.path(AzurePronunciationScoreKey.PRONUNCIATION_ASSESSMENT);
                        double wordAccuracy = wordPronNode.path(AzurePronunciationScoreKey.ACCURACY_SCORE).asDouble(0.0);
                        String errorType = wordPronNode.path(AzurePronunciationScoreKey.ERROR_TYPE).asText(AzurePronunciationScoreKey.NONE).toUpperCase();

                        wordList.add(WordAssessment.builder()
                                .word(wordStr)
                                .accuracyScore(wordAccuracy)
                                .errorType(SpeechAssessmentErrorType.valueOf(errorType))
                                .build()
                        );
                    }
                }

                return SpeechAssessment.builder()
                        .transcriptText(displayResultText)
                        .accuracyScore(accuracyScore)
                        .fluencyScore(fluencyScore)
                        .completenessScore(completenessScore)
                        .pronunciationScore(pronScore)
                        .words(wordList)
                        .build();

            } catch (IOException e) {
                // Fallback nếu parse JSON lỗi, lấy kết quả cơ bản từ SDK objects
                PronunciationAssessmentResult sdkResult =
                        PronunciationAssessmentResult.fromResult(result);
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
                    AzureSpeechApplicationErrorCode.SPEECH_AZURE_SERVICE_ERROR,
                    AzureSpeechApplicationMessageKey.SPEECH_RECOGNITION_NO_MATCH
            );
        } else if (result.getReason() == ResultReason.Canceled) {
            CancellationDetails cancellation = CancellationDetails.fromResult(result);
            throw new InfrastructureException(
                    AzureSpeechApplicationErrorCode.SPEECH_AZURE_SERVICE_ERROR,
                    AzureSpeechApplicationMessageKey.SPEECH_RECOGNITION_CANCELLED,
                    cancellation.getErrorDetails()
            );
        } else {
            throw new InfrastructureException(
                    AzureSpeechApplicationErrorCode.SPEECH_AZURE_SERVICE_ERROR,
                    AzureSpeechApplicationMessageKey.SPEECH_AZURE_SERVICE_UNKNOWN_ERROR_OCCUR
            );
        }
    }

    public SpeechAssessment mergeAssessments(List<SpeechAssessment> assessments) {
        if (assessments.isEmpty()) {
            return null;
        }
        if (assessments.size() == 1) {
            return assessments.get(0);
        }

        StringBuilder fullTranscript = new StringBuilder();
        List<WordAssessment> allWords = new ArrayList<>();

        double totalAccuracy = 0.0;
        double totalFluency = 0.0;
        double totalCompleteness = 0.0;
        double totalPronScore = 0.0;
        int totalWordCount = 0;

        for (SpeechAssessment segment : assessments) {
            if (segment.getTranscriptText() != null && !segment.getTranscriptText().isEmpty()) {
                if (fullTranscript.length() > 0) {
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
                totalWordCount += wordCount;
            } else {
                totalAccuracy += segment.getAccuracyScore();
                totalFluency += segment.getFluencyScore();
                totalCompleteness += segment.getCompletenessScore();
                totalPronScore += segment.getPronunciationScore();
                totalWordCount += 1;
            }
        }

        double finalAccuracy = totalWordCount > 0 ? totalAccuracy / totalWordCount : 0.0;
        double finalFluency = totalWordCount > 0 ? totalFluency / totalWordCount : 0.0;
        double finalCompleteness = totalWordCount > 0 ? totalCompleteness / totalWordCount : 0.0;
        double finalPronScore = totalWordCount > 0 ? totalPronScore / totalWordCount : 0.0;

        return SpeechAssessment.builder()
                .transcriptText(fullTranscript.toString())
                .accuracyScore(finalAccuracy)
                .fluencyScore(finalFluency)
                .completenessScore(finalCompleteness)
                .pronunciationScore(finalPronScore)
                .words(allWords)
                .build();
    }
}
