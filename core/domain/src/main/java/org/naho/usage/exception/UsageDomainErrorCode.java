package org.naho.usage.exception;

import org.naho.i18n.message.usage.UsageTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum UsageDomainErrorCode implements ErrorCode {
    USAGE_SUBSCRIPTION_ID_EMPTY("USA_001", UsageTitleMessageKey.USAGE_INVALID_TITLE, 400),
    USAGE_USER_ID_EMPTY("USA_002", UsageTitleMessageKey.USAGE_INVALID_TITLE, 400),
    USAGE_PERIOD_START_EMPTY("USA_003", UsageTitleMessageKey.USAGE_INVALID_TITLE, 400),
    USAGE_PERIOD_END_EMPTY("USA_004", UsageTitleMessageKey.USAGE_INVALID_TITLE, 400),
    USAGE_PERIOD_INVALID("USA_005", UsageTitleMessageKey.USAGE_INVALID_TITLE, 400),

    USAGE_LIMIT_EXCEEDED("USA_101", UsageTitleMessageKey.USAGE_FAILED_TITLE, 400),
    USAGE_AUDIO_DURATION_EXCEEDED("USA_102", UsageTitleMessageKey.USAGE_FAILED_TITLE, 400);

    private final String code;
    private final String titleKey;
    private final int statusCode;

    UsageDomainErrorCode(String code, String titleKey, int statusCode) {
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
