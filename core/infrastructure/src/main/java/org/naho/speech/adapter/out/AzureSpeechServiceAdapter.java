package org.naho.speech.adapter.out;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.shared.exception.DomainException;
import org.naho.shared.exception.InfrastructureException;
import org.naho.speech.PronunciationScoreKeys;
import org.naho.speech.config.AzureSpeechProperties;
import org.naho.speech.exception.SpeechApplicationErrorCode;
import org.naho.speech.exception.SpeechInfrastructureErrorCode;
import org.naho.speech.model.PronunciationAssessment;
import org.naho.speech.model.WordAssessment;
import org.naho.speech.port.out.SpeechAssessmentService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AzureSpeechServiceAdapter implements SpeechAssessmentService {

    private final AzureSpeechProperties properties;
    private final ObjectMapper objectMapper;

    @Override
    public PronunciationAssessment assess(byte[] audioBytes, String referenceText) {
        // 1. Tạo file tạm thời để lưu dữ liệu audio gửi lên
        File tempFile = createTempAudioFile(audioBytes);

        try {
            // 2. Cấu hình SpeechConfig và AudioConfig từ file tạm
            SpeechConfig speechConfig = SpeechConfig.fromSubscription(properties.getSubscriptionKey(), properties.getRegion());
            speechConfig.setSpeechRecognitionLanguage(properties.getLanguage());

            AudioConfig audioConfig = AudioConfig.fromWavFileInput(tempFile.getAbsolutePath());

            // 3. Khởi tạo cấu hình đánh giá phát âm (Pronunciation Assessment)
            boolean isScripted = referenceText != null && !referenceText.trim().isEmpty();
            PronunciationAssessmentConfig pronConfig = new PronunciationAssessmentConfig(
                    isScripted ? referenceText.trim() : "",
                    PronunciationAssessmentGradingSystem.HundredMark,
                    PronunciationAssessmentGranularity.Phoneme,
                    false // Tắt miscue vì miscue không được hỗ trợ ổn định ở unscripted
            );

            // Bật thêm tính năng prosody (âm điệu, ngắt nghỉ)
            pronConfig.enableProsodyAssessment();

            // Khởi tạo SpeechRecognizer
            SpeechRecognizer recognizer = new SpeechRecognizer(speechConfig, audioConfig);
            pronConfig.applyTo(recognizer);

            log.info("Sending request to Azure Speech AI. Mode: {}", isScripted ? "Scripted" : "Unscripted (Free talk)");

            // 4. Đồng bộ gọi đánh giá một lượt
            SpeechRecognitionResult result = recognizer.recognizeOnceAsync().get();

            // 5. Xử lý kết quả trả về
            PronunciationAssessment domainResult = processResult(result);

            // Giải phóng tài nguyên
            recognizer.close();
            speechConfig.close();
            audioConfig.close();
            pronConfig.close();

            return domainResult;

        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new InfrastructureException(SpeechInfrastructureErrorCode.AZURE_SPEECH_SERVICE_ERROR, "Azure Speech service connection interrupted!");
        } finally {
            // Luôn đảm bảo xóa file tạm thời để tránh tràn ổ đĩa
            if (tempFile.exists() && !tempFile.delete()) {
                log.warn("Failed to delete temp file: {}", tempFile.getAbsolutePath());
            }
        }
    }

    private File createTempAudioFile(byte[] audioBytes) {
        try {
            File tempDir = new File(System.getProperty("java.io.tmpdir"));
            File tempFile = File.createTempFile("naho_assess_" + UUID.randomUUID(), ".wav", tempDir);
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(audioBytes);
            }
            return tempFile;
        } catch (IOException e) {
            throw new DomainException(SpeechInfrastructureErrorCode.AUDIO_FILE_INVALID, "Could not cache uploaded audio file!");
        }
    }

    private PronunciationAssessment processResult(SpeechRecognitionResult result) {
        if (result.getReason() == ResultReason.RecognizedSpeech) {
            // Lấy kết quả thô dạng JSON để parsing chi tiết đầy đủ metrics
            String jsonResult = result.getProperties().getProperty(PropertyId.SpeechServiceResponse_JsonResult);
            log.debug("Azure Speech JSON response: {}", jsonResult);

            try {
                JsonNode root = objectMapper.readTree(jsonResult);

                // Mặc định kết quả nằm trong mảng NBest
                JsonNode nBestNode = root.path(PronunciationScoreKeys.N_BEST).get(0);
                if (nBestNode == null) {
                    throw new DomainException(SpeechInfrastructureErrorCode.SPEECH_RECOGNITION_NO_MATCH, "No speech matched from audio!");
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
            NoMatchDetails noMatch = NoMatchDetails.fromResult(result);
            log.warn("No match detail reason: {}", noMatch.getReason());
            throw new DomainException(SpeechInfrastructureErrorCode.SPEECH_RECOGNITION_NO_MATCH, "No speech could be recognized. Please try again with clear speech!");
        } else if (result.getReason() == ResultReason.Canceled) {
            CancellationDetails cancellation = CancellationDetails.fromResult(result);
            throw new DomainException(SpeechInfrastructureErrorCode.SPEECH_RECOGNITION_CANCELED, "Azure Speech API canceled: " + cancellation.getErrorDetails());
        } else {
            throw new DomainException(SpeechInfrastructureErrorCode.AZURE_SPEECH_SERVICE_ERROR, "Unknown speech service error occurred.");
        }
    }
}