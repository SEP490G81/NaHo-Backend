package org.naho.point.specification;

import jakarta.persistence.criteria.Path;
import org.naho.point.constant.PointAmountType;
import org.naho.point.entity.PointHistoryEntity;
import org.naho.point.type.PointTransactionType;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public final class PointHistorySpecification {
    private PointHistorySpecification() {
    }

    // filter by user id
    public static Specification<PointHistoryEntity> hasUserId(Long userId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("user").get("id"), userId);
    }

    // filter by transaction type, if transaction type is null, get all
    public static Specification<PointHistoryEntity> hasTransactionType(PointTransactionType transactionType) {
        return (root, query, criteriaBuilder) -> {
            if (transactionType == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("transactionType"), transactionType);
        };
    }

    // filter by amount type (which point is positive or negative), if amount type is null, get all
    public static Specification<PointHistoryEntity> hasAmountType(PointAmountType amountType) {
        return (root, query, criteriaBuilder) -> {
            if (amountType == null) {
                return criteriaBuilder.conjunction();
            }
            return switch (amountType) {
                case POSITIVE -> criteriaBuilder.greaterThanOrEqualTo(root.get("point"), 0);
                case NEGATIVE -> criteriaBuilder.lessThanOrEqualTo(root.get("point"), 0);
            };
        };
    }

    // filter by transaction time
    public static Specification<PointHistoryEntity> transactionTimeBetween(
            Instant transactionTimeFrom,
            Instant transactionTimeTo
    ) {
        return (root, query, criteriaBuilder) -> {
            Path<Instant> transactionTime = root.get("transactionTime");

            // if both transaction time from and to are null, get all
            if (transactionTimeFrom == null && transactionTimeTo == null) {
                return criteriaBuilder.conjunction();
            }

            // if only transaction time from is null
            // get all transaction time to is equal or greater than transaction time from
            if (transactionTimeFrom != null && transactionTimeTo == null) {
                return criteriaBuilder.greaterThanOrEqualTo(
                        transactionTime,
                        transactionTimeFrom
                );
            }

            // if only transaction time to is null
            // get all transaction time from is equal or less than transaction time to
            if (transactionTimeFrom == null) {
                return criteriaBuilder.lessThanOrEqualTo(
                        transactionTime,
                        transactionTimeTo
                );
            }

            // if both transaction time from and to are not null
            // get all transaction time between transaction time from and transaction time to
            return criteriaBuilder.between(transactionTime, transactionTimeFrom, transactionTimeTo);
        };
    }
}
