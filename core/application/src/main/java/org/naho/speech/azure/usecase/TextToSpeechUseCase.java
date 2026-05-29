package org.naho.speech.azure.usecase;

import org.naho.shared.exception.ApplicationException;
import org.naho.speech.azure.command.TextToSpeechCommand;
import org.naho.speech.azure.constant.AzureSpeechApplicationMessageKey;
import org.naho.speech.azure.exception.AzureSpeechApplicationErrorCode;
import org.naho.speech.azure.port.in.TextToSpeechInputPort;
import org.naho.speech.azure.port.out.TextToSpeechServicePort;
import org.naho.speech.azure.result.AudioSpeechResult;

public class TextToSpeechUseCase implements TextToSpeechInputPort {
    private static final int TEXT_MAX_LENGTH = 5000;
    private final TextToSpeechServicePort textToSpeechServicePort;

    public TextToSpeechUseCase(TextToSpeechServicePort textToSpeechServicePort) {
        this.textToSpeechServicePort = textToSpeechServicePort;
    }

    @Override
    public AudioSpeechResult execute(TextToSpeechCommand command) {
        if (command.text() == null || command.text().isBlank()) {
            throw new ApplicationException(
                    AzureSpeechApplicationErrorCode.SPEECH_TEXT_NOT_VALID,
                    AzureSpeechApplicationMessageKey.SPEECH_TEXT_BLANK
            );
        }

        // Giới hạn ký tự tối đa tùy chỉnh
        if (command.text().length() > TEXT_MAX_LENGTH) {
            throw new ApplicationException(
                    AzureSpeechApplicationErrorCode.SPEECH_TEXT_NOT_VALID,
                    AzureSpeechApplicationMessageKey.SPEECH_TEXT_INVALID_RANGE,
                    TEXT_MAX_LENGTH
            );
        }

        return textToSpeechServicePort.textToSpeech(
                command.text().trim(),
                command.voiceName(),
                command.language()
        );
    }
}
