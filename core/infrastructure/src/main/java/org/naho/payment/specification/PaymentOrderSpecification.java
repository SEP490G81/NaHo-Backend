package org.naho.payment.specification;

import jakarta.persistence.criteria.Path;
import org.naho.payment.entity.PaymentOrderEntity;
import org.naho.payment.type.PaymentProvider;
import org.naho.payment.type.PaymentStatus;
import org.naho.shared.specification.SpecificationHelper;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public final class PaymentOrderSpecification {

    private PaymentOrderSpecification() {
    }

    public static Specification<PaymentOrderEntity> hasOrderCode(String orderCode) {
        return ((root, query, criteriaBuilder) -> {
            if (orderCode == null || orderCode.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            String pattern = "%" + SpecificationHelper.escapeLikePattern(orderCode.toLowerCase()) + "%";
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("orderCode")),
                    pattern,
                    '\\'
            );
        });
    }

    public static Specification<PaymentOrderEntity> hasProviderTransactionId(String providerTxnId) {
        return ((root, query, criteriaBuilder) -> {
            if (providerTxnId == null || providerTxnId.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            String pattern = "%" + SpecificationHelper.escapeLikePattern(providerTxnId.toLowerCase()) + "%";
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("providerTransactionId")),
                    pattern,
                    '\\'
            );
        });
    }

    public static Specification<PaymentOrderEntity> hasUserId(Long userId) {
        return ((root, query, criteriaBuilder) -> {
            if (userId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("userId"), userId);
        });
    }

    public static Specification<PaymentOrderEntity> hasStatus(PaymentStatus status) {
        return ((root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        });
    }

    public static Specification<PaymentOrderEntity> hasProvider(PaymentProvider provider) {
        return ((root, query, criteriaBuilder) -> {
            if (provider == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("provider"), provider);
        });
    }

    public static Specification<PaymentOrderEntity> createdTimeBetween(Instant from, Instant to) {
        return ((root, query, criteriaBuilder) -> {
            Path<Instant> createdTime = root.get("createdTime");

            if (from == null && to == null) {
                return criteriaBuilder.conjunction();
            }

            if (from != null && to == null) {
                return criteriaBuilder.greaterThanOrEqualTo(createdTime, from);
            }

            if (from == null) {
                return criteriaBuilder.lessThanOrEqualTo(createdTime, to);
            }

            return criteriaBuilder.between(createdTime, from, to);
        });
    }
}
