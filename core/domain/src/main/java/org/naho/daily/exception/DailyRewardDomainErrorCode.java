package org.naho.daily.exception;

import org.naho.i18n.message.daily.DailyRewardTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum DailyRewardDomainErrorCode implements ErrorCode {
    DAILY_REWARD_YEAR_MONTH_REQUIRED(
            "DAILY_REWARD_001",
            DailyRewardTitleMessageKey.DAILY_REWARD_CREATION_FAILED_TITLE,
            400
    ),
    DAILY_REWARD_YEAR_MONTH_INVALID_FORMAT(
            "DAILY_REWARD_002",
            DailyRewardTitleMessageKey.DAILY_REWARD_CREATION_FAILED_TITLE,
            400
    ),
    DAILY_REWARD_DAY_OF_MONTH_REQUIRED(
            "DAILY_REWARD_003",
            DailyRewardTitleMessageKey.DAILY_REWARD_CREATION_FAILED_TITLE,
            400
    ),
    DAILY_REWARD_DAY_OF_MONTH_INVALID(
            "DAILY_REWARD_004",
            DailyRewardTitleMessageKey.DAILY_REWARD_CREATION_FAILED_TITLE,
            400
    ),
    DAILY_REWARD_NOT_FOUND(
            "DAILY_REWARD_005",
            DailyRewardTitleMessageKey.DAILY_REWARD_NOT_FOUND_TITLE,
            404
    );

    private final String code;
    private final String titleKey;
    private final int statusCode;

    DailyRewardDomainErrorCode(String code, String titleKey, int statusCode) {
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
