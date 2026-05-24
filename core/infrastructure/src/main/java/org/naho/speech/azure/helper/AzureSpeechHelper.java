package org.naho.speech.azure.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.cognitiveservices.speech.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.shared.constant.FileExtension;
import org.naho.shared.exception.InfrastructureException;
import org.naho.speech.azure.constant.PronunciationScoreKeys;
import org.naho.speech.azure.exception.
        SpeechInfrastructureErrorCode;
import org.naho.speech.model.PronunciationAssessment;
import org.naho.speech.model.WordAssessment;
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
public class AzureSpeechHelper {
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
                    SpeechInfrastructureErrorCode.AUDIO_FILE_INVALID,
                    "Failed to create temporary audio file"
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

    public PronunciationAssessment processResult(SpeechRecognitionResult result) {
        if (result.getReason() == ResultReason.RecognizedSpeech) {
            // Lấy kết quả thô dạng JSON để parsing chi tiết đầy đủ metrics
            String jsonResult = result.getProperties().getProperty(PropertyId.SpeechServiceResponse_JsonResult);

            try {
                JsonNode root = objectMapper.readTree(jsonResult);

                // Mặc định kết quả nằm trong mảng NBest
                JsonNode nBestNode = root.path(PronunciationScoreKeys.N_BEST).get(0);
                if (nBestNode == null) {
                    throw new InfrastructureException(
                            SpeechInfrastructureErrorCode.SPEECH_RECOGNITION_NO_MATCH, "No speech matched from audio!");
                }

                String displayResultText = nBestNode.path(PronunciationScoreKeys.DISPLAY).asText();
                JsonNode pronNode = nBestNode.path(PronunciationScoreKeys.PRONUNCIATION_ASSESSMENT);

                double accuracyScore = pronNode.path(PronunciationScoreKeys.ACCURACY_SCORE).asDouble(0.0);
                double fluencyScore = pronNode.path(PronunciationScoreKeys.FLUENCY_SCORE).asDouble(0.0);
                double completenessScore = pronNode.path(PronunciationScoreKeys.COMPLETENESS_SCORE).asDouble(0.0);
                double pronScore = pronNode.path(PronunciationScoreKeys.PRON_SCORE).asDouble(0.0);

                // Lấy chi tiết từ (Word level)
                List<WordAssessment> wordList = new ArrayList<>();
                JsonNode wordsNode = nBestNode.path(PronunciationScoreKeys.WORDS);
                if (wordsNode.isArray()) {
                    for (JsonNode wordNode : wordsNode) {
                        String wordStr = wordNode.path(PronunciationScoreKeys.WORD).asText();
                        JsonNode wordPronNode = wordNode.path(PronunciationScoreKeys.PRONUNCIATION_ASSESSMENT);
                        double wordAccuracy = wordPronNode.path(PronunciationScoreKeys.ACCURACY_SCORE).asDouble(0.0);
                        String errorType = wordPronNode.path(PronunciationScoreKeys.ERROR_TYPE).asText(PronunciationScoreKeys.NONE);

                        wordList.add(new WordAssessment(wordStr, wordAccuracy, errorType));
                    }
                }

                return PronunciationAssessment.builder()
                        .transcript(displayResultText)
                        .accuracyScore(accuracyScore)
                        .fluencyScore(fluencyScore)
                        .completenessScore(completenessScore)
                        .pronunciationScore(pronScore)
                        .words(wordList)
                        .build();

            } catch (IOException e) {
                // Fallback nếu parse JSON lỗi, lấy kết quả cơ bản từ SDK objects
                com.microsoft.cognitiveservices.speech.PronunciationAssessmentResult sdkResult =
                        com.microsoft.cognitiveservices.speech.PronunciationAssessmentResult.fromResult(result);
                return PronunciationAssessment.builder()
                        .transcript(result.getText())
                        .accuracyScore(sdkResult.getAccuracyScore())
                        .fluencyScore(sdkResult.getFluencyScore())
                        .completenessScore(sdkResult.getCompletenessScore())
                        .pronunciationScore(sdkResult.getPronunciationScore())
                        .words(List.of())
                        .build();
            }
        } else if (result.getReason() == ResultReason.NoMatch) {
            throw new InfrastructureException(
                    SpeechInfrastructureErrorCode.SPEECH_RECOGNITION_NO_MATCH,
                    "No speech could be recognized. Please try again with clear speech!"
            );
        } else if (result.getReason() == ResultReason.Canceled) {
            CancellationDetails cancellation = CancellationDetails.fromResult(result);
            throw new InfrastructureException(
                    SpeechInfrastructureErrorCode.SPEECH_RECOGNITION_CANCELED,
                    "Azure Speech API canceled: " + cancellation.getErrorDetails()
            );
        } else {
            throw new InfrastructureException(
                    SpeechInfrastructureErrorCode.AZURE_SPEECH_SERVICE_ERROR,
                    "Unknown speech service error occurred!"
            );
        }
    }
}
