package org.naho.speech.exception;

import org.naho.shared.exception.ErrorCode;

public enum SpeechErrorCode implements ErrorCode {
    AUDIO_DATA_REQUIRED("SPEECH_001", "audio.data.not.be.empty"),
    ;

    private final String code;
    private final String title;

    SpeechErrorCode(String code, String title) {
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
