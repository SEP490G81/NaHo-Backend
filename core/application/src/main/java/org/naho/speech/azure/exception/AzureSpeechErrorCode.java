package org.naho.speech.azure.exception;

import org.naho.i18n.message.speech.SpeechTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum AzureSpeechErrorCode implements ErrorCode {
    SPEECH_AUDIO_NOT_VALID(
            "SPEECH_A001",
            SpeechTitleMessageKey.SPEECH_AUDIO_NOT_VALID_TITLE
    ),
    SPEECH_AZURE_SERVICE_ERROR(
            "SPEECH_A002",
            SpeechTitleMessageKey.SPEECH_AZURE_SERVICE_ERROR_TITLE
    ),
    SPEECH_TEXT_NOT_VALID(
            "SPEECH_A003",
            SpeechTitleMessageKey.SPEECH_TEXT_NOT_VALID_TITLE
    );

    private final String code;
    private final String titleKey;

    AzureSpeechErrorCode(String code, String titleKey) {
        this.code = code;
        this.titleKey = titleKey;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getTitleKey() {
        return titleKey;
    }
}