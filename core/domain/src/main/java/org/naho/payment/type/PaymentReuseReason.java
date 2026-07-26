package org.naho.payment.type;

public enum PaymentReuseReason {
    CREATED,
    IDEMPOTENCY_REPLAY,
    ACTIVE_PENDING_REUSED
}
