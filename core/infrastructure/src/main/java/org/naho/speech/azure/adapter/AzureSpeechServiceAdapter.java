package org.naho.speech.azure.adapter;

import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import com.microsoft.cognitiveservices.speech.audio.AudioInputStream;
import com.microsoft.cognitiveservices.speech.audio.AudioStreamFormat;
import com.microsoft.cognitiveservices.speech.audio.PushAudioInputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.i18n.message.speech.SpeechDetailMessageKey;
import org.naho.shared.exception.InfrastructureException;
import org.naho.speech.azure.command.SpeechAssessmentCommand;
import org.naho.speech.azure.constant.AzureSpeechConfigProperties;
import org.naho.speech.azure.exception.AzureSpeechErrorCode;
import org.naho.speech.azure.helper.AzureSpeechServiceHelper;
import org.naho.speech.azure.port.out.AzureSpeechServicePort;
import org.naho.speech.model.SpeechAssessment;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AzureSpeechServiceAdapter implements AzureSpeechServicePort {

    private final AzureSpeechConfigProperties properties;
    private final AzureSpeechServiceHelper azureSpeechServiceHelper;

    @Override
    public SpeechAssessment assess(SpeechAssessmentCommand command) {
        byte[] rawAudioBytes = command.audioBytes();
        String referenceText = command.referenceText();

        /*
         * Validate dung lượng audio ngay từ đầu.
         * Mục đích:
         * - Chặn file quá lớn trước khi ghi ra temp file.
         * - Bảo vệ server khỏi request upload bất thường.
         */
        azureSpeechServiceHelper.validateAudioSize(rawAudioBytes);

        /*
         * Lưu file audio gốc ra temp file.
         * FFmpeg sẽ đọc file này và stream PCM ra stdout pipe.
         */
        Path inputFile = azureSpeechServiceHelper.createTempInputAudioFile(rawAudioBytes);

        /*
         * PushAudioInputStream là stream Azure SDK dùng để nhận PCM audio.
         * Azure sẽ đọc từng chunk được ghi vào stream này giống như đang nhận audio từ microphone.
         */
        AudioStreamFormat format =
                AudioStreamFormat.getWaveFormatPCM(16000, (short) 16, (short) 1);

        PushAudioInputStream pushStream =
                AudioInputStream.createPushStream(format);

        SpeechRecognizer recognizer = null;
        CompletableFuture<Void> ffmpegStreamingFuture = null;

        try {
            /*
             * Dùng ffprobe để lấy duration từ file gốc.
             * Không tính duration từ PCM byte[] nữa vì bản streaming không tạo PCM byte[] hoàn chỉnh.
             */
            double durationSeconds =
                    azureSpeechServiceHelper.probeAudioDurationSeconds(inputFile);

            /*
             * Validate thời lượng audio.
             * Ví dụ: không cho vượt quá 120 giây.
             */
            azureSpeechServiceHelper.validateAudioDuration(durationSeconds);

            /*
             * Tính timeout động theo duration.
             * Ví dụ: duration + 30s, nhưng không vượt quá max timeout.
             */
            long timeoutSeconds =
                    azureSpeechServiceHelper.calculateProcessingTimeoutSeconds(durationSeconds);

            /*
             * Các config này có thể dùng try-with-resources vì chúng không cần sống lâu hơn scope xử lý hiện tại.
             * Java sẽ tự close theo thứ tự ngược lại:
             * pronunciationConfig -> audioConfig -> speechConfig
             */
            try (
                    SpeechConfig speechConfig =
                            SpeechConfig.fromSubscription(
                                    properties.getSubscriptionKey(),
                                    properties.getRegion()
                            );

                    AudioConfig audioConfig =
                            AudioConfig.fromStreamInput(pushStream);

                    PronunciationAssessmentConfig pronunciationConfig =
                            azureSpeechServiceHelper
                                    .createPronunciationAssessmentConfig(referenceText)
            ) {
                /*
                 * Cấu hình ngôn ngữ nhận diện.
                 * Ví dụ: ja-JP, en-US, vi-VN...
                 */
                speechConfig.setSpeechRecognitionLanguage(properties.getLanguage());

                /*
                 * SpeechRecognizer là object chính chạy continuous recognition.
                 * Không đặt nó trong try-with-resources trực tiếp vì nó có callback async.
                 * Ta sẽ chủ động close recognizer trong finally.
                 */
                recognizer = new SpeechRecognizer(speechConfig, audioConfig);

                /*
                 * Apply Pronunciation Assessment vào recognizer.
                 */
                pronunciationConfig.applyTo(recognizer);

                /*
                 * Danh sách kết quả từng segment Azure trả về.
                 * Continuous recognition có thể trả nhiều đoạn nhỏ, sau đó mình merge lại.
                 */
                List<SpeechAssessment> segmentAssessments =
                        Collections.synchronizedList(new ArrayList<>());

                /*
                 * Lưu lỗi phát sinh trong callback async.
                 * Vì lỗi trong callback không tự throw ra thread chính.
                 */
                List<Throwable> errors =
                        Collections.synchronizedList(new ArrayList<>());

                /*
                 * Semaphore dùng để báo cho thread chính biết Azure đã kết thúc nhận diện.
                 * Khi canceled hoặc sessionStopped xảy ra thì release semaphore.
                 */
                Semaphore stopRecognitionSemaphore = new Semaphore(0);

                /*
                 * Event recognized:
                 * Azure gọi event này mỗi khi nhận diện được một segment speech.
                 */
                recognizer.recognized.addEventListener((s, e) -> {
                    if (e.getResult().getReason() == ResultReason.RecognizedSpeech) {
                        try {
                            SpeechAssessment segmentResult =
                                    azureSpeechServiceHelper.processResult(e.getResult());

                            segmentAssessments.add(segmentResult);

                        } catch (Exception ex) {
                            log.error("Error processing recognition segment result", ex);
                            errors.add(ex);
                        }
                    }
                });

                /*
                 * Event canceled:
                 * Xảy ra khi Azure hủy recognition.
                 * Nếu reason là Error thì cần đưa lỗi vào errors để thread chính xử lý.
                 */
                recognizer.canceled.addEventListener((s, e) -> {
                    CancellationDetails cancellation =
                            CancellationDetails.fromResult(e.getResult());

                    if (cancellation.getReason() == CancellationReason.Error) {
                        log.error(
                                "Azure Speech continuous recognition error: {}",
                                cancellation.getErrorDetails()
                        );

                        errors.add(new InfrastructureException(
                                AzureSpeechErrorCode.SPEECH_AZURE_SERVICE_ERROR,
                                SpeechDetailMessageKey.SPEECH_AZURE_API_ERROR,
                                cancellation.getErrorDetails()
                        ));
                    }

                    stopRecognitionSemaphore.release();
                });

                /*
                 * Event sessionStopped:
                 * Azure gọi event này khi session recognition kết thúc bình thường.
                 */
                recognizer.sessionStopped.addEventListener((s, e) -> {
                    log.info("Speech recognition session stopped.");
                    stopRecognitionSemaphore.release();
                });

                /*
                 * Start Azure continuous recognition trước.
                 * Sau khi Azure sẵn sàng đọc stream, ta mới bắt đầu đẩy audio từ FFmpeg vào pushStream.
                 */
                recognizer.startContinuousRecognitionAsync().get();

                /*
                 * Bắt đầu FFmpeg streaming async:
                 * inputFile -> FFmpeg convert PCM 16k mono -> stdout pipe -> pushStream -> Azure.
                 *
                 * Điểm tối ưu:
                 * - Không chờ FFmpeg convert xong toàn bộ file.
                 * - Azure nhận và xử lý audio ngay khi FFmpeg sinh ra chunk PCM đầu tiên.
                 */
                ffmpegStreamingFuture =
                        azureSpeechServiceHelper.streamAzurePcmByFfmpegAsync(
                                inputFile,
                                pushStream
                        );

                /*
                 * Chờ Azure báo kết thúc recognition.
                 * Timeout được tính theo duration thay vì hard-code 60s.
                 */
                boolean completed =
                        stopRecognitionSemaphore.tryAcquire(
                                timeoutSeconds,
                                TimeUnit.SECONDS
                        );

                if (!completed) {
                    log.warn(
                            "Continuous recognition timed out after {} seconds.",
                            timeoutSeconds
                    );

                    /*
                     * Khi timeout, chủ động stop recognizer để tránh Azure SDK tiếp tục chạy ngầm.
                     */
                    recognizer.stopContinuousRecognitionAsync().get();

                    throw new InfrastructureException(
                            AzureSpeechErrorCode.SPEECH_AZURE_SERVICE_ERROR,
                            SpeechDetailMessageKey.SPEECH_AUDIO_PROCESSING_TIMEOUT
                    );
                }

                /*
                 * Chờ FFmpeg streaming thread hoàn tất.
                 * Chỉ chờ thêm thời gian ngắn vì tại thời điểm này Azure session đã stopped/canceled.
                 */
                if (ffmpegStreamingFuture != null) {
                    ffmpegStreamingFuture.get(5, TimeUnit.SECONDS);
                }

                /*
                 * Nếu trong callback có lỗi thì throw ra thread chính.
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
                 * Không có segment nào nghĩa là Azure không nhận diện được nội dung.
                 */
                if (segmentAssessments.isEmpty()) {
                    throw new InfrastructureException(
                            AzureSpeechErrorCode.SPEECH_AZURE_SERVICE_ERROR,
                            SpeechDetailMessageKey.SPEECH_RECOGNITION_NO_MATCH
                    );
                }

                /*
                 * Merge nhiều segment thành một kết quả cuối cùng.
                 */
                return azureSpeechServiceHelper.mergeAssessments(segmentAssessments);
            }

        } catch (InterruptedException e) {
            /*
             * InterruptedException nghĩa là thread hiện tại bị interrupt.
             * Phải set lại interrupt flag để tầng trên biết thread đã bị interrupt.
             */
            Thread.currentThread().interrupt();

            throw new InfrastructureException(
                    AzureSpeechErrorCode.SPEECH_AZURE_SERVICE_ERROR,
                    SpeechDetailMessageKey.SPEECH_AZURE_CONNECTION_INTERRUPTED
            );

        } catch (ExecutionException e) {
            /*
             * ExecutionException thường bọc lỗi bên trong Future/Azure async call.
             */
            Throwable cause = e.getCause();

            if (cause instanceof InfrastructureException infrastructureException) {
                throw infrastructureException;
            }

            throw new InfrastructureException(
                    AzureSpeechErrorCode.SPEECH_AZURE_SERVICE_ERROR,
                    SpeechDetailMessageKey.SPEECH_AZURE_EXECUTION_FAILED
            );

        } catch (TimeoutException e) {
            /*
             * TimeoutException có thể xảy ra khi chờ ffmpegStreamingFuture.get(5s).
             */
            throw new InfrastructureException(
                    AzureSpeechErrorCode.SPEECH_AZURE_SERVICE_ERROR,
                    SpeechDetailMessageKey.SPEECH_AUDIO_PROCESSING_TIMEOUT
            );

        } catch (InfrastructureException e) {
            /*
             * Giữ nguyên lỗi nghiệp vụ/hạ tầng đã được map sẵn errorCode + messageKey.
             */
            throw e;

        } catch (Exception e) {
            /*
             * Fallback cuối cùng cho lỗi không mong muốn.
             */
            log.error("Unexpected Azure Speech assessment error", e);

            throw new InfrastructureException(
                    AzureSpeechErrorCode.SPEECH_AZURE_SERVICE_ERROR,
                    SpeechDetailMessageKey.SPEECH_AZURE_SERVICE_UNKNOWN_ERROR_OCCUR
            );

        } finally {
            /*
             * Nếu FFmpeg streaming vẫn chưa xong thì cancel để tránh thread/process chạy thừa.
             */
            if (ffmpegStreamingFuture != null && !ffmpegStreamingFuture.isDone()) {
                ffmpegStreamingFuture.cancel(true);
            }

            /*
             * Đóng pushStream để báo Azure không còn audio input.
             */
            try {
                pushStream.close();
            } catch (Exception ignored) {
            }

            /*
             * Đóng recognizer sau cùng vì recognizer phụ thuộc vào audioConfig/pushStream callback.
             */
            if (recognizer != null) {
                recognizer.close();
            }

            /*
             * Xóa temp input file.
             */
            try {
                Files.deleteIfExists(inputFile);
            } catch (Exception ignored) {
            }
        }
    }
}