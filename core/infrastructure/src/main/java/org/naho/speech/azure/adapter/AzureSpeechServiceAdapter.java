package org.naho.speech.azure.adapter;

import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.shared.exception.InfrastructureException;
import org.naho.speech.azure.command.SpeechAssessmentCommand;
import org.naho.speech.azure.constant.AzureSpeechApplicationMessageKey;
import org.naho.speech.azure.constant.AzureSpeechConfigProperties;
import org.naho.speech.azure.exception.AzureSpeechApplicationErrorCode;
import org.naho.speech.azure.helper.AzureSpeechServiceHelper;
import org.naho.speech.azure.port.out.AzureSpeechServicePort;
import org.naho.speech.model.SpeechAssessment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AzureSpeechServiceAdapter implements AzureSpeechServicePort {

    private final AzureSpeechConfigProperties properties;
    private final AzureSpeechServiceHelper azureSpeechServiceHelper;

    @Override
    public SpeechAssessment assess(SpeechAssessmentCommand command) {
        byte[] audioBytes = command.audioBytes();
        String referenceText = command.referenceText();

        // 1. Tạo file tạm thời để lưu dữ liệu audio gửi lên
        File tempFile = azureSpeechServiceHelper.createTempAudioFile(audioBytes);

        try {
            // 2. Cấu hình SpeechConfig và AudioConfig từ file tạm
            SpeechConfig speechConfig = SpeechConfig.fromSubscription(properties.getSubscriptionKey(), properties.getRegion());
            speechConfig.setSpeechRecognitionLanguage(properties.getLanguage());

            AudioConfig audioConfig = AudioConfig.fromWavFileInput(tempFile.getAbsolutePath());

            // 3. Khởi tạo cấu hình đánh giá phát âm (Pronunciation Assessment)
            PronunciationAssessmentConfig config =
                    azureSpeechServiceHelper.createPronunciationAssessmentConfig(referenceText);

            // Khởi tạo SpeechRecognizer
            SpeechRecognizer recognizer = new SpeechRecognizer(speechConfig, audioConfig);
            config.applyTo(recognizer);

            // Danh sách chứa kết quả của từng phân đoạn nhận diện
            List<SpeechAssessment> segmentAssessments = Collections.synchronizedList(new ArrayList<>());
            List<Throwable> errors = Collections.synchronizedList(new ArrayList<>());
            Semaphore stopRecognitionSemaphore = new Semaphore(0);

            // Lắng nghe sự kiện nhận diện thành công phân đoạn
            recognizer.recognized.addEventListener((s, e) -> {
                if (e.getResult().getReason() == ResultReason.RecognizedSpeech) {
                    try {
                        SpeechAssessment segmentResult = azureSpeechServiceHelper.processResult(e.getResult());
                        segmentAssessments.add(segmentResult);
                    } catch (Exception ex) {
                        log.error("Error processing recognition segment result", ex);
                    }
                }
            });

            // Lắng nghe sự kiện bị hủy / lỗi kết nối
            recognizer.canceled.addEventListener((s, e) -> {
                CancellationDetails cancellation = CancellationDetails.fromResult(e.getResult());
                if (cancellation.getReason() == CancellationReason.Error) {
                    log.error("Azure Speech continuous recognition error: {}", cancellation.getErrorDetails());
                    errors.add(new InfrastructureException(
                            AzureSpeechApplicationErrorCode.SPEECH_AZURE_SERVICE_ERROR,
                            "Azure Speech API error: " + cancellation.getErrorDetails()
                    ));
                }
                stopRecognitionSemaphore.release();
            });

            // Lắng nghe sự kiện dừng phiên làm việc (khi đọc hết file âm thanh đầu vào)
            recognizer.sessionStopped.addEventListener((s, e) -> {
                log.info("Speech recognition session stopped.");
                stopRecognitionSemaphore.release();
            });

            // 4. Bắt đầu nhận diện liên tục
            recognizer.startContinuousRecognitionAsync().get();

            // Đợi quá trình nhận diện hoàn thành (timeout tối đa 60 giây)
            boolean completed = stopRecognitionSemaphore.tryAcquire(60, TimeUnit.SECONDS);
            if (!completed) {
                log.warn("Continuous recognition timed out after 60 seconds.");
            }

            // Dừng nhận diện liên tục và giải phóng các tài nguyên SDK
            recognizer.stopContinuousRecognitionAsync().get();
            recognizer.close();
            speechConfig.close();
            audioConfig.close();
            config.close();

            // Kiểm tra nếu có lỗi nghiêm trọng xảy ra trong quá trình nhận diện
            if (!errors.isEmpty()) {
                Throwable firstError = errors.get(0);
                if (firstError instanceof RuntimeException) {
                    throw (RuntimeException) firstError;
                } else {
                    throw new InfrastructureException(AzureSpeechApplicationErrorCode.SPEECH_AZURE_SERVICE_ERROR, firstError.getMessage());
                }
            }

            if (segmentAssessments.isEmpty()) {
                throw new InfrastructureException(
                        AzureSpeechApplicationErrorCode.SPEECH_AZURE_SERVICE_ERROR,
                        AzureSpeechApplicationMessageKey.SPEECH_RECOGNITION_NO_MATCH
                );
            }

            // 5. Tổng hợp các phân đoạn thành kết quả cuối cùng
            return azureSpeechServiceHelper.mergeAssessments(segmentAssessments);

        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new InfrastructureException(
                    AzureSpeechApplicationErrorCode.SPEECH_AZURE_SERVICE_ERROR,
                    AzureSpeechApplicationMessageKey.SPEECH_AZURE_CONNECTION_INTERRUPTED
            );
        } finally {
            // Luôn đảm bảo xóa file tạm thời để tránh tràn ổ đĩa
            if (tempFile.exists() && !tempFile.delete()) {
                log.warn("Failed to delete temp file: {}", tempFile.getAbsolutePath());
            }
        }
    }
}