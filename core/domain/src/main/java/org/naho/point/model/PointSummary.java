package org.naho.point.model;

import org.naho.i18n.message.point.PointSummaryDetailMessageKey;
import org.naho.point.exception.PointSummaryDomainErrorCode;
import org.naho.shared.exception.DomainException;

public class PointSummary {

    private final Long id;
    private final Long userId;
    private final Double totalPoint;

    private PointSummary(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.totalPoint = builder.totalPoint;
    }

    public static PointSummary init(Long userId) {
        return builder()
                .userId(userId)
                .totalPoint(0.0)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Long id;
        private Long userId;
        private Double totalPoint;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder totalPoint(Double totalPoint) {
            this.totalPoint = totalPoint;
            return this;
        }

        public PointSummary build() {
            if (userId == null) {
                throw new DomainException(
                        PointSummaryDomainErrorCode.POINT_SUMMARY_USER_ID_NOT_VALID,
                        PointSummaryDetailMessageKey.POINT_SUMMARY_USER_ID_BLANK
                );
            }
            
            if (totalPoint == null) {
                throw new DomainException(
                        PointSummaryDomainErrorCode.POINT_SUMMARY_TOTAL_POINT_NOT_VALID,
                        PointSummaryDetailMessageKey.POINT_SUMMARY_TOTAL_POINT_BLANK
                );
            }
            return new PointSummary(this);
        }
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Double getTotalPoint() {
        return totalPoint;
    }
}
