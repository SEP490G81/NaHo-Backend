package org.naho.speech.azure.exception;

import org.naho.i18n.message.speech.SpeechTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum AzureSpeechErrorCode implements ErrorCode {
    SPEECH_AUDIO_NOT_VALID(
            "SPEECH_A001",
            SpeechTitleMessageKey.SPEECH_AUDIO_NOT_VALID_TITLE,
            400
    ),
    SPEECH_AZURE_SERVICE_ERROR(
            "SPEECH_A002",
            SpeechTitleMessageKey.SPEECH_AZURE_SERVICE_ERROR_TITLE,
            502
    ),
    SPEECH_TEXT_NOT_VALID(
            "SPEECH_A003",
            SpeechTitleMessageKey.SPEECH_TEXT_NOT_VALID_TITLE,
            400
    );

    private final String code;
    private final String titleKey;
    private final int statusCode;

    AzureSpeechErrorCode(String code, String titleKey, int statusCode) {
        this.code = code;
        this.titleKey = titleKey;
        this.statusCode = statusCode;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getTitleKey() {
        return titleKey;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }
}