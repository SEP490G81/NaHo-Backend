package org.naho.daily.exception;

import org.naho.i18n.message.daily.DailyRewardTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum DailyRewardErrorCode implements ErrorCode {
    DAILY_REWARD_ALREADY_EXISTS(
            "DAILY_REWARD_A001",
            DailyRewardTitleMessageKey.DAILY_REWARD_CREATION_FAILED_TITLE,
            400
    );

    private final String code;
    private final String titleKey;
    private final int statusCode;

    DailyRewardErrorCode(String code, String titleKey, int statusCode) {
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
