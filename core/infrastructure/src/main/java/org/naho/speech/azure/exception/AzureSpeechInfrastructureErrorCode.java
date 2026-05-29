package org.naho.speech.azure.exception;

import org.naho.shared.exception.ErrorCode;
import org.naho.speech.azure.constant.AzureSpeechInfrastructureMessageKey;

public enum AzureSpeechInfrastructureErrorCode implements ErrorCode {
    AZURE_SPEECH_SERVICE_ERROR("SPEECH_I001", AzureSpeechInfrastructureMessageKey.AZURE_SPEECH_SERVICE_ERROR),
    AZURE_SPEECH_CONNECTION_TIMEOUT("SPEECH_I002", AzureSpeechInfrastructureMessageKey.AZURE_SPEECH_CONNECTION_TIMEOUT),
    AZURE_SPEECH_TEMP_FILE_ERROR("SPEECH_I003", AzureSpeechInfrastructureMessageKey.AZURE_SPEECH_TEMP_FILE_ERROR),
    AZURE_SPEECH_RECOGNITION_CANCELLED("SPEECH_I004", AzureSpeechInfrastructureMessageKey.AZURE_SPEECH_RECOGNITION_CANCELLED);

    private final String code;
    private final String titleKey;

    AzureSpeechInfrastructureErrorCode(String code, String titleKey) {
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
