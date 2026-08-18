package org.naho.speech.azure.adapter;

import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import com.microsoft.cognitiveservices.speech.audio.AudioInputStream;
import com.microsoft.cognitiveservices.speech.audio.PushAudioInputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.i18n.message.speech.SpeechDetailMessageKey;
import org.naho.shared.exception.InfrastructureException;
import org.naho.speech.azure.command.SpeechAssessmentCommand;
import org.naho.speech.azure.constant.AzureSpeechConfigProperties;
import org.naho.speech.azure.exception.AzureSpeechErrorCode;
import org.naho.speech.azure.helper.AzureSpeechServiceHelper;
import org.naho.speech.azure.model.SpeechAssessment;
import org.naho.speech.azure.port.out.AzureSpeechServicePort;
import org.springframework.stereotype.Service;

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
    public SpeechAssessment assessAudio(SpeechAssessmentCommand command) {
        byte[] audioBytes = command.audioBytes();
        String referenceText = command.referenceText();

        /*
         * Tính timeout động theo duration của file audio đã được validate trước đó.
         */
        long timeoutSeconds = azureSpeechServiceHelper.calculateProcessingTimeoutSeconds(command.duration());

        /*
         * PushAudioInputStream nhận trực tiếp byte[] định dạng WAV chuẩn PCM.
         * Azure Speech SDK tự động phân tích WAV header và đọc dữ liệu PCM.
         */
        PushAudioInputStream pushStream = AudioInputStream.createPushStream();
        SpeechRecognizer recognizer = null;

        try (
                SpeechConfig speechConfig = SpeechConfig.fromSubscription(
                        properties.getSubscriptionKey(),
                        properties.getRegion()
                );
                AudioConfig audioConfig = AudioConfig.fromStreamInput(pushStream);
                PronunciationAssessmentConfig pronunciationConfig =
                        azureSpeechServiceHelper.createPronunciationAssessmentConfig(referenceText)
        ) {
            /*
             * Cấu hình ngôn ngữ nhận diện.
             */
            speechConfig.setSpeechRecognitionLanguage(properties.getLanguage());

            /*
             * Khởi tạo SpeechRecognizer với continuous recognition.
             */
            recognizer = new SpeechRecognizer(speechConfig, audioConfig);
            pronunciationConfig.applyTo(recognizer);

            List<SpeechAssessment> segmentAssessments = Collections.synchronizedList(new ArrayList<>());
            List<Throwable> errors = Collections.synchronizedList(new ArrayList<>());
            Semaphore stopRecognitionSemaphore = new Semaphore(0);

            recognizer.recognized.addEventListener((s, e) -> {
                if (e.getResult().getReason() == ResultReason.RecognizedSpeech) {
                    try {
                        SpeechAssessment segmentResult = azureSpeechServiceHelper.processResult(e.getResult());
                        segmentAssessments.add(segmentResult);
                    } catch (Exception ex) {
                        log.error("Error processing recognition segment result", ex);
                        errors.add(ex);
                    }
                }
            });

            recognizer.canceled.addEventListener((s, e) -> {
                CancellationDetails cancellation = CancellationDetails.fromResult(e.getResult());
                if (cancellation.getReason() == CancellationReason.Error) {
                    log.error("Azure Speech continuous recognition error: {}", cancellation.getErrorDetails());
                    errors.add(new InfrastructureException(
                            AzureSpeechErrorCode.SPEECH_AZURE_SERVICE_ERROR,
                            SpeechDetailMessageKey.SPEECH_AZURE_API_ERROR,
                            cancellation.getErrorDetails()
                    ));
                }
                stopRecognitionSemaphore.release();
            });

            recognizer.sessionStopped.addEventListener((s, e) -> {
                log.info("Speech recognition session stopped.");
                stopRecognitionSemaphore.release();
            });

            /*
             * Bắt đầu continuous recognition trước khi ghi dữ liệu vào pushStream.
             */
            recognizer.startContinuousRecognitionAsync().get();

            /*
             * Ghi toàn bộ dữ liệu audio WAV vào pushStream và đóng stream để báo hiệu kết thúc (EOF).
             */
            pushStream.write(audioBytes);
            pushStream.close();

            /*
             * Chờ Azure báo kết thúc recognition theo dynamic timeout.
             */
            boolean completed = stopRecognitionSemaphore.tryAcquire(timeoutSeconds, TimeUnit.SECONDS);

            if (!completed) {
                log.warn("Continuous recognition timed out after {} seconds.", timeoutSeconds);
                recognizer.stopContinuousRecognitionAsync().get();
                throw new InfrastructureException(
                        AzureSpeechErrorCode.SPEECH_AZURE_SERVICE_ERROR,
                        SpeechDetailMessageKey.SPEECH_AUDIO_PROCESSING_TIMEOUT
                );
            }

            /*
             * Nếu có lỗi trong callback async thì ném ngoại lệ.
             */
            if (!errors.isEmpty()) {
                Throwable firstError = errors.get(0);
                if (firstError instanceof InfrastructureException infrastructureException) {
                    throw infrastructureException;
                }
                throw new InfrastructureException(
                        AzureSpeechErrorCode.SPEECH_AZURE_SERVICE_ERROR,
                        SpeechDetailMessageKey.SPEECH_AZURE_API_ERROR,
                        firstError.getMessage()
                );
            }

            /*
             * Không có segment nào nghĩa là không nhận diện được nội dung phát âm.
             */
            if (segmentAssessments.isEmpty()) {
                throw new InfrastructureException(
                        AzureSpeechErrorCode.SPEECH_AZURE_SERVICE_ERROR,
                        SpeechDetailMessageKey.SPEECH_RECOGNITION_NO_MATCH
                );
            }

            return azureSpeechServiceHelper.mergeAssessments(segmentAssessments);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InfrastructureException(
                    AzureSpeechErrorCode.SPEECH_AZURE_SERVICE_ERROR,
                    SpeechDetailMessageKey.SPEECH_AZURE_CONNECTION_INTERRUPTED
            );
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof InfrastructureException infrastructureException) {
                throw infrastructureException;
            }
            throw new InfrastructureException(
                    AzureSpeechErrorCode.SPEECH_AZURE_SERVICE_ERROR,
                    SpeechDetailMessageKey.SPEECH_AZURE_EXECUTION_FAILED,
                    cause != null ? cause.getMessage() : e.getMessage()
            );
        } catch (InfrastructureException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected Azure Speech assessment error", e);
            throw new InfrastructureException(
                    AzureSpeechErrorCode.SPEECH_AZURE_SERVICE_ERROR,
                    SpeechDetailMessageKey.SPEECH_AZURE_SERVICE_UNKNOWN_ERROR_OCCUR,
                    e.getMessage()
            );
        } finally {
            try {
                pushStream.close();
            } catch (Exception ignored) {
            }

            if (recognizer != null) {
                recognizer.close();
            }
        }
    }
}