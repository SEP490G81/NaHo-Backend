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

        azureSpeechServiceHelper.validateAudioSize(rawAudioBytes);

        Path inputFile = azureSpeechServiceHelper.createTempInputAudioFile(rawAudioBytes);

        double durationSeconds = azureSpeechServiceHelper.probeAudioDurationSeconds(inputFile);
        azureSpeechServiceHelper.validateAudioDuration(durationSeconds);
        long timeoutSeconds = azureSpeechServiceHelper.calculateProcessingTimeoutSeconds(durationSeconds);

        AudioStreamFormat format = AudioStreamFormat.getWaveFormatPCM(16000, (short) 16, (short) 1);
        PushAudioInputStream pushStream = AudioInputStream.createPushStream(format);

        SpeechConfig speechConfig = null;
        AudioConfig audioConfig = null;
        PronunciationAssessmentConfig pronunciationConfig = null;
        SpeechRecognizer recognizer = null;
        CompletableFuture<Void> ffmpegStreamingFuture = null;

        try {
            speechConfig = SpeechConfig.fromSubscription(properties.getSubscriptionKey(), properties.getRegion());
            speechConfig.setSpeechRecognitionLanguage(properties.getLanguage());

            audioConfig = AudioConfig.fromStreamInput(pushStream);
            pronunciationConfig = azureSpeechServiceHelper.createPronunciationAssessmentConfig(referenceText);

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

            recognizer.startContinuousRecognitionAsync().get();

            ffmpegStreamingFuture = azureSpeechServiceHelper.streamAzurePcmByFfmpegAsync(inputFile, pushStream);

            boolean completed = stopRecognitionSemaphore.tryAcquire(timeoutSeconds, TimeUnit.SECONDS);
            if (!completed) {
                log.warn("Continuous recognition timed out after {} seconds.", timeoutSeconds);
                recognizer.stopContinuousRecognitionAsync().get();

                throw new InfrastructureException(
                        AzureSpeechErrorCode.SPEECH_AZURE_SERVICE_ERROR,
                        SpeechDetailMessageKey.SPEECH_AUDIO_PROCESSING_TIMEOUT
                );
            }

            if (ffmpegStreamingFuture != null) {
                ffmpegStreamingFuture.get(5, TimeUnit.SECONDS);
            }

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
                    SpeechDetailMessageKey.SPEECH_AZURE_EXECUTION_FAILED
            );
        } catch (InfrastructureException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected Azure Speech assessment error", e);
            throw new InfrastructureException(
                    AzureSpeechErrorCode.SPEECH_AZURE_SERVICE_ERROR,
                    SpeechDetailMessageKey.SPEECH_AZURE_SERVICE_UNKNOWN_ERROR_OCCUR
            );
        } finally {
            if (ffmpegStreamingFuture != null && !ffmpegStreamingFuture.isDone()) {
                ffmpegStreamingFuture.cancel(true);
            }
            try {
                pushStream.close();
            } catch (Exception ignored) {
            }
            if (recognizer != null) recognizer.close();
            if (audioConfig != null) audioConfig.close();
            if (pronunciationConfig != null) pronunciationConfig.close();
            if (speechConfig != null) speechConfig.close();
            try {
                Files.deleteIfExists(inputFile);
            } catch (Exception ignored) {
            }
        }
    }
}
