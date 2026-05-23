package org.naho.speech.exception;

import org.naho.shared.exception.ErrorCode;


public enum SpeechInfrastructureErrorCode implements ErrorCode {
    AZURE_SPEECH_SERVICE_ERROR("SPEECH_002", "speech.azure.service.error"),
    AUDIO_FILE_INVALID("SPEECH_001", "speech.audio.file.invalid"),
    SPEECH_RECOGNITION_CANCELED("SPEECH_003", "speech.recognition.canceled"),
    SPEECH_RECOGNITION_NO_MATCH("SPEECH_004", "speech.recognition.no.match");

    private final String code;
    private final String title;

    SpeechInfrastructureErrorCode(String code, String title) {
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