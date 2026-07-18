package org.naho.payment.exception;

import org.naho.i18n.message.payment.PaymentTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum PaymentDomainErrorCode implements ErrorCode {
    PAYMENT_ORDER_CODE_EMPTY("PAY_001", PaymentTitleMessageKey.PAYMENT_ORDER_INVALID_TITLE, 400),
    PAYMENT_USER_ID_EMPTY("PAY_002", PaymentTitleMessageKey.PAYMENT_ORDER_INVALID_TITLE, 400),
    PAYMENT_PLAN_ID_EMPTY("PAY_003", PaymentTitleMessageKey.PAYMENT_ORDER_INVALID_TITLE, 400),
    PAYMENT_AMOUNT_EMPTY("PAY_004", PaymentTitleMessageKey.PAYMENT_ORDER_INVALID_TITLE, 400),
    PAYMENT_EXPIRATION_INVALID("PAY_005", PaymentTitleMessageKey.PAYMENT_ORDER_INVALID_TITLE, 400),
    PAYMENT_INVALID_STATE("PAY_006", PaymentTitleMessageKey.PAYMENT_FAILED_TITLE, 400),
    PAYMENT_DUPLICATE_TXN("PAY_007", PaymentTitleMessageKey.PAYMENT_FAILED_TITLE, 400),
    PAYMENT_ORDER_ID_EMPTY("PAY_008", PaymentTitleMessageKey.PAYMENT_FAILED_TITLE, 400);

    private final String code;
    private final String titleKey;
    private final int statusCode;

    PaymentDomainErrorCode(String code, String titleKey, int statusCode) {
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
