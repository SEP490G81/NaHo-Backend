package org.naho.payment.model;

import org.naho.i18n.message.payment.PaymentDetailMessageKey;
import org.naho.payment.exception.PaymentDomainErrorCode;
import org.naho.payment.type.PaymentProvider;
import org.naho.shared.exception.DomainException;

import java.time.Instant;
import java.util.Map;

public class PaymentTransaction {
    private final Long id;
    private final Long paymentOrderId;
    private final PaymentProvider provider;
    private final String providerTransactionId;
    private final Money amount;
    private final boolean successful;
    private final Instant providerTransactionTime;
    private final Map<String, String> metadata;
    private final Instant createdTime; //Thời gian tạo log giao dịch

    private PaymentTransaction(Builder builder) {
        this.id = builder.id;
        this.paymentOrderId = builder.paymentOrderId;
        this.provider = builder.provider;
        this.providerTransactionId = builder.providerTransactionId;
        this.amount = builder.amount;
        this.successful = builder.successful;
        this.providerTransactionTime = builder.providerTransactionTime;
        this.metadata = builder.metadata == null ? Map.of() : Map.copyOf(builder.metadata);
        this.createdTime = builder.createdTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static PaymentTransaction received(
            Long paymentOrderId,
            PaymentProvider provider,
            String providerTransactionId,
            Money amount,
            boolean successful,
            Instant providerTransactionTime,
            Map<String, String> metadata,
            Instant now) {
        return builder()
                .paymentOrderId(paymentOrderId)
                .provider(provider)
                .providerTransactionId(providerTransactionId)
                .amount(amount)
                .successful(successful)
                .providerTransactionTime(providerTransactionTime)
                .metadata(metadata)
                .createdTime(now)
                .build();
    }

    public Long getId() {
        return id;
    }

    public Long getPaymentOrderId() {
        return paymentOrderId;
    }

    public PaymentProvider getProvider() {
        return provider;
    }

    public String getProviderTransactionId() {
        return providerTransactionId;
    }

    public Money getAmount() {
        return amount;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public Instant getProviderTransactionTime() {
        return providerTransactionTime;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public Instant getCreatedTime() {
        return createdTime;
    }

    public static final class Builder {
        private Long id;
        private Long paymentOrderId;
        private PaymentProvider provider;
        private String providerTransactionId;
        private Money amount;
        private boolean successful;
        private Instant providerTransactionTime;
        private Map<String, String> metadata;
        private Instant createdTime;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder paymentOrderId(Long paymentOrderId) {
            this.paymentOrderId = paymentOrderId;
            return this;
        }

        public Builder provider(PaymentProvider provider) {
            this.provider = provider;
            return this;
        }

        public Builder providerTransactionId(String providerTransactionId) {
            this.providerTransactionId = providerTransactionId;
            return this;
        }

        public Builder amount(Money amount) {
            this.amount = amount;
            return this;
        }

        public Builder successful(boolean successful) {
            this.successful = successful;
            return this;
        }

        public Builder providerTransactionTime(Instant providerTransactionTime) {
            this.providerTransactionTime = providerTransactionTime;
            return this;
        }

        public Builder metadata(Map<String, String> metadata) {
            this.metadata = metadata;
            return this;
        }

        public Builder createdTime(Instant createdTime) {
            this.createdTime = createdTime;
            return this;
        }

        public PaymentTransaction build() {
            if (paymentOrderId == null) {
                throw new DomainException(PaymentDomainErrorCode.PAYMENT_ORDER_ID_EMPTY,
                        PaymentDetailMessageKey.PAYMENT_PLAN_ID_EMPTY); // reusing plan id empty for generic key
            }
            if (provider == null) {
                throw new DomainException(PaymentDomainErrorCode.PAYMENT_INVALID_STATE,
                        PaymentDetailMessageKey.PAYMENT_INVALID_STATE);
            }
            if (providerTransactionId == null || providerTransactionId.isBlank()) {
                throw new DomainException(PaymentDomainErrorCode.PAYMENT_ORDER_CODE_EMPTY,
                        PaymentDetailMessageKey.PAYMENT_ORDER_CODE_EMPTY);
            }
            if (amount == null) {
                throw new DomainException(PaymentDomainErrorCode.PAYMENT_AMOUNT_EMPTY,
                        PaymentDetailMessageKey.PAYMENT_AMOUNT_EMPTY);
            }
            if (createdTime == null) {
                createdTime = Instant.now();
            }
            return new PaymentTransaction(this);
        }
    }
}
