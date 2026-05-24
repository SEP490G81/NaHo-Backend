package org.naho.speech.azure.exception;

import org.naho.shared.exception.ErrorCode;

public enum SpeechApplicationErrorCode implements ErrorCode {
    AUDIO_FILE_INVALID("SPEECH_001", "speech.audio.file.invalid");
    private final String code;
    private final String title;

    SpeechApplicationErrorCode(String code, String title) {
        this.code = code;
        this.title = title;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getTitle() {
        return title;
    }
}