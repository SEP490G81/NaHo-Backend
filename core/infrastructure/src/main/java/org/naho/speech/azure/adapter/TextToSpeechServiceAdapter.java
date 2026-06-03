package org.naho.speech.azure.adapter;

import com.microsoft.cognitiveservices.speech.*;
import lombok.RequiredArgsConstructor;
import org.naho.shared.exception.InfrastructureException;
import org.naho.speech.azure.constant.AzureSpeechApplicationMessageKey;
import org.naho.speech.azure.constant.AzureSpeechConfigProperties;
import org.naho.speech.azure.constant.AzureSpeechContentType;
import org.naho.speech.azure.exception.AzureSpeechApplicationErrorCode;
import org.naho.speech.azure.helper.TextToSpeechServiceHelper;
import org.naho.speech.azure.port.out.TextToSpeechServicePort;
import org.naho.speech.azure.result.AudioSpeechResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class TextToSpeechServiceAdapter implements TextToSpeechServicePort {
    private final AzureSpeechConfigProperties property;
    private final TextToSpeechServiceHelper textToSpeechServiceHelper;

    @Override
    public AudioSpeechResult textToSpeech(String text, String voiceName, String language) {
        // 1. Cấu hình SpeechConfig
        SpeechConfig speechConfig = SpeechConfig.fromSubscription(property.getSubscriptionKey(), property.getRegion());

        String cleanLanguage = textToSpeechServiceHelper.sanitizeInput(language);
        String cleanVoiceName = textToSpeechServiceHelper.sanitizeInput(voiceName);

        // Cấu hình ngôn ngữ tổng hợp
        String targetLanguage = (cleanLanguage != null && !cleanLanguage.isBlank()) ? cleanLanguage : property.getLanguage();
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

            // Thực hiện tổng hợp đồng bộ
            result = synthesizer.SpeakTextAsync(text).get();

            // 3. Xử lý kết quả trả về từ Azure
            if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
                byte[] audioData = result.getAudioData();

                return new AudioSpeechResult(
                        audioData,
                        AzureSpeechContentType.AUDIO_WAV
                );

            } else if (result.getReason() == ResultReason.Canceled) {
                SpeechSynthesisCancellationDetails cancellation = SpeechSynthesisCancellationDetails.fromResult(result);

                throw new InfrastructureException(
                        AzureSpeechApplicationErrorCode.SPEECH_AZURE_SERVICE_ERROR,
                        AzureSpeechApplicationMessageKey.SPEECH_SYNTHESIS_CANCELLED,
                        cancellation.getErrorDetails()
                );
            } else {
                throw new InfrastructureException(
                        AzureSpeechApplicationErrorCode.SPEECH_AZURE_SERVICE_ERROR,
                        AzureSpeechApplicationMessageKey.SPEECH_SYNTHESIS_UNKNOWN_ERROR_OCCUR
                );
            }

        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new InfrastructureException(
                    AzureSpeechApplicationErrorCode.SPEECH_AZURE_SERVICE_ERROR,
                    AzureSpeechApplicationMessageKey.SPEECH_AZURE_CONNECTION_INTERRUPTED
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
