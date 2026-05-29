package org.naho.speech.azure.exception;

import org.naho.shared.exception.ErrorCode;
import org.naho.speech.azure.constant.AzureSpeechApplicationMessageKey;

public enum AzureSpeechApplicationErrorCode implements ErrorCode {
    SPEECH_AUDIO_NOT_VALID(
            "SPEECH_A001",
            AzureSpeechApplicationMessageKey.SPEECH_AUDIO_NOT_VALID_TITLE
    ),
    SPEECH_AZURE_SERVICE_ERROR(
            "SPEECH_A002",
            AzureSpeechApplicationMessageKey.SPEECH_AZURE_SERVICE_ERROR_TITLE
    ),
    SPEECH_TEXT_NOT_VALID(
            "SPEECH_A003",
            AzureSpeechApplicationMessageKey.SPEECH_TEXT_NOT_VALID_TITLE
    );

    private final String code;
    private final String titleKey;

    AzureSpeechApplicationErrorCode(String code, String titleKey) {
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