package org.naho.speech.azure.exception;

import org.naho.i18n.message.speech.SpeechTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum SpeechAssessmentDomainErrorCode implements ErrorCode {
    SPEECH_SCORE_NULL(
            "SPEECH_D001",
            SpeechTitleMessageKey.SPEECH_AUDIO_NOT_VALID_TITLE,
            400
    );

    private final String code;
    private final String titleKey;
    private final int statusCode;

    SpeechAssessmentDomainErrorCode(String code, String titleKey, int statusCode) {
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
