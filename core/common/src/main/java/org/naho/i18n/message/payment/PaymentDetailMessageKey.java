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
    public static final String PAYMENT_ORDER_NOT_BELONG_TO_USER = "payment.order.not_belong_to_user";
    public static final String PAYMENT_ORDER_GET_ALL_SUCCESS = "payment.order.get_all_success";
    public static final String PAYMENT_SUBSCRIPTION_SAME_OR_LOWER_TIER = "payment.subscription.same_or_lower_tier";
    public static final String PAYMENT_SUBSCRIPTION_MAX_TIER_REACHED = "payment.subscription.max_tier_reached";
    public static final String PAYMENT_SUBSCRIPTION_UPGRADE_SUCCESS = "payment.subscription.upgrade_success";

    public static final String PAYMENT_ORDER_CREATION_SUCCESS = "payment.order.creation_success";
    public static final String PAYMENT_ORDER_GET_SUCCESS = "payment.order.get_success";
    public static final String PAYMENT_ORDER_CANCEL_SUCCESS = "payment.order.cancel_success";

    public static final String PAYMENT_PAGE_INVALID = "payment.page.invalid";
    public static final String PAYMENT_SIZE_INVALID = "payment.size.invalid";
    public static final String PAYMENT_CREATED_TIME_RANGE_INVALID = "payment.created_time_range.invalid";

    private PaymentDetailMessageKey() {
    }
}
