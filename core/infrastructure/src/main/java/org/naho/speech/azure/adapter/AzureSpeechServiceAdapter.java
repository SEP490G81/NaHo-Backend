package org.naho.speech.azure.adapter;

import com.microsoft.cognitiveservices.speech.PronunciationAssessmentConfig;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.shared.exception.InfrastructureException;
import org.naho.speech.azure.command.SpeechAssessmentCommand;
import org.naho.speech.azure.config.AzureSpeechProperties;
import org.naho.speech.azure.exception.SpeechInfrastructureErrorCode;
import org.naho.speech.azure.helper.AzureSpeechHelper;
import org.naho.speech.azure.port.out.AzureSpeechService;
import org.naho.speech.model.PronunciationAssessment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AzureSpeechServiceAdapter implements AzureSpeechService {

    private final AzureSpeechProperties properties;
    private final AzureSpeechHelper azureSpeechHelper;

    @Override
    public PronunciationAssessment assess(SpeechAssessmentCommand command) {
        byte[] audioBytes = command.audioBytes();
        String referenceText = command.referenceText();

        // 1. Tạo file tạm thời để lưu dữ liệu audio gửi lên
        File tempFile = azureSpeechHelper.createTempAudioFile(audioBytes);

        try {
            // 2. Cấu hình SpeechConfig và AudioConfig từ file tạm
            SpeechConfig speechConfig = SpeechConfig.fromSubscription(properties.getSubscriptionKey(), properties.getRegion());
            speechConfig.setSpeechRecognitionLanguage(properties.getLanguage());

            AudioConfig audioConfig = AudioConfig.fromWavFileInput(tempFile.getAbsolutePath());

            // 3. Khởi tạo cấu hình đánh giá phát âm (Pronunciation Assessment)
            PronunciationAssessmentConfig config =
                    azureSpeechHelper.createPronunciationAssessmentConfig(referenceText);

            // Khởi tạo SpeechRecognizer
            SpeechRecognizer recognizer = new SpeechRecognizer(speechConfig, audioConfig);
            config.applyTo(recognizer);

            // 4. Đồng bộ gọi đánh giá một lượt
            SpeechRecognitionResult result = recognizer.recognizeOnceAsync().get();

            // 5. Xử lý kết quả trả về
            PronunciationAssessment domainResult = azureSpeechHelper.processResult(result);

            // Giải phóng tài nguyên
            recognizer.close();
            speechConfig.close();
            audioConfig.close();
            config.close();

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
}