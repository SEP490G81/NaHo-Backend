package org.naho.subscription.exception;

import org.naho.i18n.message.subscription.SubscriptionTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum SubscriptionDomainErrorCode implements ErrorCode {
    PLAN_CODE_EMPTY("SUB_001", SubscriptionTitleMessageKey.SUBSCRIPTION_INVALID_TITLE, 400),
    PLAN_NAME_EMPTY("SUB_002", SubscriptionTitleMessageKey.SUBSCRIPTION_INVALID_TITLE, 400),
    PLAN_PRICE_EMPTY("SUB_003", SubscriptionTitleMessageKey.SUBSCRIPTION_INVALID_TITLE, 400),
    PLAN_DURATION_INVALID("SUB_004", SubscriptionTitleMessageKey.SUBSCRIPTION_INVALID_TITLE, 400),
    PLAN_QUOTA_EMPTY("SUB_005", SubscriptionTitleMessageKey.SUBSCRIPTION_INVALID_TITLE, 400),
    PLAN_TIER_EMPTY("SUB_006", SubscriptionTitleMessageKey.SUBSCRIPTION_INVALID_TITLE, 400),
    PLAN_STATUS_EMPTY("SUB_007", SubscriptionTitleMessageKey.SUBSCRIPTION_INVALID_TITLE, 400),

    SUBSCRIPTION_USER_ID_EMPTY("SUB_101", SubscriptionTitleMessageKey.SUBSCRIPTION_FAILED_TITLE, 400),
    SUBSCRIPTION_PLAN_ID_EMPTY("SUB_102", SubscriptionTitleMessageKey.SUBSCRIPTION_FAILED_TITLE, 400),
    SUBSCRIPTION_STATUS_EMPTY("SUB_103", SubscriptionTitleMessageKey.SUBSCRIPTION_FAILED_TITLE, 400),
    SUBSCRIPTION_TIME_INVALID("SUB_104", SubscriptionTitleMessageKey.SUBSCRIPTION_FAILED_TITLE, 400),
    SUBSCRIPTION_START_TIME_EMPTY("SUB_105", SubscriptionTitleMessageKey.SUBSCRIPTION_FAILED_TITLE, 400),
    SUBSCRIPTION_END_TIME_EMPTY("SUB_106", SubscriptionTitleMessageKey.SUBSCRIPTION_FAILED_TITLE, 400);

    private final String code;
    private final String titleKey;
    private final int statusCode;

    SubscriptionDomainErrorCode(String code, String titleKey, int statusCode) {
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
