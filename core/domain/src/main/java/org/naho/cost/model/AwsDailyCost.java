package org.naho.cost.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AwsDailyCost {

    private Long id;
    private LocalDate recordDate;
    private BigDecimal costAmount;
    private String currency;

    private AwsDailyCost(Builder builder) {
        this.id = builder.id;
        this.recordDate = builder.recordDate;
        this.costAmount = builder.costAmount;
        this.currency = builder.currency;
    }

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

        public AwsDailyCost build() {
            return new AwsDailyCost(this);
        }
    }

    public Long getId() {
        return id;
    }

    public LocalDate getRecordDate() {
        return recordDate;
    }

    public BigDecimal getCostAmount() {
        return costAmount;
    }

    public String getCurrency() {
        return currency;
    }
}
