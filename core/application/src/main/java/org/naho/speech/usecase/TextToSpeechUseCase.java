package org.naho.speech.usecase;

import org.naho.shared.exception.ApplicationException;
import org.naho.speech.command.TextToSpeechCommand;
import org.naho.speech.exception.SpeechApplicationErrorCode;
import org.naho.speech.mapper.AudioSpeechMapper;
import org.naho.speech.model.AudioSpeech;
import org.naho.speech.port.in.TextToSpeechInputPort;
import org.naho.speech.port.out.TextToSpeechService;
import org.naho.speech.result.AudioSpeechResult;

public class TextToSpeechUseCase implements TextToSpeechInputPort {

    private final TextToSpeechService textToSpeechService;
    private final AudioSpeechMapper audioSpeechMapper;

    public TextToSpeechUseCase(TextToSpeechService textToSpeechService, AudioSpeechMapper audioSpeechMapper) {
        this.textToSpeechService = textToSpeechService;
        this.audioSpeechMapper = audioSpeechMapper;
    }

    @Override
    public AudioSpeechResult execute(TextToSpeechCommand command) {
        // 1. Validation nghiệp vụ tầng ứng dụng
        if (command.text() == null || command.text().trim().isEmpty()) {
            throw new ApplicationException(SpeechApplicationErrorCode.TEXT_REQUIRED, "Text content is required for speech synthesis!");
        }

        if (command.text().length() > 5000) { // Giới hạn ký tự tối đa tùy chỉnh
            throw new ApplicationException(SpeechApplicationErrorCode.TEXT_TOO_LONG, "Text is too long (maximum 5000 characters)!");
        }

        // 2. Gọi outbound port hạ tầng để tổng hợp giọng nói
        AudioSpeech audioSpeech = textToSpeechService.textToSpeech(
                command.text().trim(),
                command.voiceName(),
                command.language()
        );

        // 3. Ánh xạ sang Result DTO để trả ra tầng ngoài
        return audioSpeechMapper.modelToResult(audioSpeech);
    }
}