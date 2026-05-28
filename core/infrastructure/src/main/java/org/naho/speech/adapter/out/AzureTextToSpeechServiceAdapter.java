package org.naho.speech.adapter.out;

import com.microsoft.cognitiveservices.speech.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.shared.exception.InfrastructureException;
import org.naho.speech.config.AzureSpeechProperties;
import org.naho.speech.exception.SpeechInfrastructureErrorCode;
import org.naho.speech.model.AudioSpeech;
import org.naho.speech.port.out.TextToSpeechService;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AzureTextToSpeechServiceAdapter implements TextToSpeechService {

    private final AzureSpeechProperties properties;

    private String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        String sanitized = input.trim();
        if ((sanitized.startsWith("\"") && sanitized.endsWith("\"")) ||
                (sanitized.startsWith("'") && sanitized.endsWith("'"))) {
            sanitized = sanitized.substring(1, sanitized.length() - 1).trim();
        }
        return sanitized;
    }

    @Override
    public AudioSpeech textToSpeech(String text, String voiceName, String language) {
        // 1. Cấu hình SpeechConfig
        SpeechConfig speechConfig = SpeechConfig.fromSubscription(properties.getSubscriptionKey(), properties.getRegion());

        String cleanLanguage = sanitizeInput(language);
        String cleanVoiceName = sanitizeInput(voiceName);

        // Cấu hình ngôn ngữ tổng hợp
        String targetLanguage = (cleanLanguage != null && !cleanLanguage.isBlank()) ? cleanLanguage : properties.getLanguage();
        speechConfig.setSpeechSynthesisLanguage(targetLanguage);

        // Cấu hình giọng nói tổng hợp (Mặc định dùng ja-JP-NanamiNeural cho tiếng Nhật)
        String targetVoice = (cleanVoiceName != null && !cleanVoiceName.isBlank()) ? cleanVoiceName : "ja-JP-NanamiNeural";
        speechConfig.setSpeechSynthesisVoiceName(targetVoice);

        // Thiết lập định dạng đầu ra chất lượng cao Riff16Khz16BitMonoPcm (chuẩn WAV)
        speechConfig.setSpeechSynthesisOutputFormat(SpeechSynthesisOutputFormat.Riff16Khz16BitMonoPcm);

        // 2. Khởi tạo SpeechSynthesizer với AudioConfig = null để tổng hợp trong bộ nhớ (In-memory stream)
        SpeechSynthesizer synthesizer = null;
        SpeechSynthesisResult result = null;

        try {
            synthesizer = new SpeechSynthesizer(speechConfig, null);
            log.info("Sending synthesis request to Azure Speech AI. Voice: {}, Language: {}, Text length: {}",
                    targetVoice, targetLanguage, text.length());

            // Thực hiện tổng hợp đồng bộ
            result = synthesizer.SpeakTextAsync(text).get();

            // 3. Xử lý kết quả trả về từ Azure
            if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
                byte[] audioData = result.getAudioData();
                log.info("Successfully synthesized text to speech. Audio size: {} bytes", audioData.length);

                return AudioSpeech.builder()
                        .audioData(audioData)
                        .contentType("audio/wav") // Phù hợp với định dạng Riff16Khz16BitMonoPcm
                        .build();

            } else if (result.getReason() == ResultReason.Canceled) {
                SpeechSynthesisCancellationDetails cancellation = SpeechSynthesisCancellationDetails.fromResult(result);
                log.error("Azure TTS Synthesis canceled. Reason: {}, Error details: {}",
                        cancellation.getReason(), cancellation.getErrorDetails());

                throw new InfrastructureException(
                        SpeechInfrastructureErrorCode.AZURE_TTS_CANCELED,
                        "Synthesis canceled: " + cancellation.getErrorDetails()
                );
            } else {
                throw new InfrastructureException(
                        SpeechInfrastructureErrorCode.AZURE_TTS_SYNTHESIS_FAILED,
                        "Unknown synthesis error occurred."
                );
            }

        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Azure Speech synthesis process was interrupted", e);
            throw new InfrastructureException(
                    SpeechInfrastructureErrorCode.AZURE_SPEECH_SERVICE_ERROR,
                    "Azure Speech service connection was interrupted."
            );
        } finally {
            // Giải phóng bắt buộc các tài nguyên native JNI của C++ SDK
            if (result != null) {
                result.close();
            }
            if (synthesizer != null) {
                synthesizer.close();
            }
            speechConfig.close();
        }
    }
}