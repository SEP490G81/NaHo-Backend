package org.naho.subscription.exception;

import org.naho.i18n.message.subscription.SubscriptionTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum SubscriptionErrorCode implements ErrorCode {
    PLAN_NOT_FOUND("SUB_A001", SubscriptionTitleMessageKey.SUBSCRIPTION_INVALID_TITLE, 404),
    PLAN_UNAVAILABLE("SUB_A002", SubscriptionTitleMessageKey.SUBSCRIPTION_INVALID_TITLE, 400),
    ALREADY_ACTIVE_HIGHER_OR_EQUAL_PLAN("SUB_A003", SubscriptionTitleMessageKey.SUBSCRIPTION_INVALID_TITLE, 400);

    private final String code;
    private final String titleKey;
    private final int statusCode;

    SubscriptionErrorCode(String code, String titleKey, int statusCode) {
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
