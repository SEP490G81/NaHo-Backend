package org.naho.i18n.message.payment;

public final class PaymentDetailMessageKey {
    public static final String PAYMENT_ORDER_CODE_EMPTY = "payment.order.code.empty";
    public static final String PAYMENT_USER_ID_EMPTY = "payment.user_id.empty";
    public static final String PAYMENT_PLAN_ID_EMPTY = "payment.plan_id.empty";
    public static final String PAYMENT_AMOUNT_EMPTY = "payment.amount.empty";
    public static final String PAYMENT_EXPIRATION_INVALID = "payment.expiration.invalid";
    public static final String PAYMENT_INVALID_STATE = "payment.invalid.state";
    public static final String PAYMENT_DUPLICATE_TXN = "payment.duplicate.txn";
    public static final String PAYMENT_IDEMPOTENCY_KEY_INVALID = "payment.idempotency_key.invalid";
    public static final String PAYMENT_IDEMPOTENCY_KEY_REUSED = "payment.idempotency_key.reused";
    public static final String PAYMENT_ACTIVE_ORDER_EXISTS = "payment.active_order_exists";
    public static final String PAYMENT_ORDER_NOT_FOUND = "payment.order.not_found";

    private PaymentDetailMessageKey() {
    }
}
