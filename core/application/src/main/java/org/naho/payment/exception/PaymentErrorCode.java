package org.naho.payment.exception;

import org.naho.i18n.message.payment.PaymentTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum PaymentErrorCode implements ErrorCode {
    PAYMENT_ORDER_NOT_FOUND("PAY_A001", PaymentTitleMessageKey.PAYMENT_FAILED_TITLE, 404),
    GATEWAY_RESOLVE_FAILED("PAY_A002", PaymentTitleMessageKey.PAYMENT_FAILED_TITLE, 500),
    PLAN_NOT_FOUND("PAY_A003", PaymentTitleMessageKey.PAYMENT_FAILED_TITLE, 404),
    PAYMENT_INVALID_STATE("PAY_A004", PaymentTitleMessageKey.PAYMENT_ORDER_INVALID_TITLE, 400);

    private final String code;
    private final String titleKey;
    private final int statusCode;

    PaymentErrorCode(String code, String titleKey, int statusCode) {
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
