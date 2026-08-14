package org.naho.cost.result;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AwsDailyCostResult(
        Long id,
        LocalDate recordDate,
        BigDecimal costAmount,
        String currency
) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Long id;
        private LocalDate recordDate;
        private BigDecimal costAmount;
        private String currency;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder recordDate(LocalDate recordDate) {
            this.recordDate = recordDate;
            return this;
        }

        public Builder costAmount(BigDecimal costAmount) {
            this.costAmount = costAmount;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public AwsDailyCostResult build() {
            return new AwsDailyCostResult(
                    id,
                    recordDate,
                    costAmount,
                    currency
            );
        }
    }
}
