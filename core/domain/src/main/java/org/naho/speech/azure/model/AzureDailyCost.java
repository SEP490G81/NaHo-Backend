package org.naho.speech.azure.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AzureDailyCost {
    private Long id;
    private LocalDate recordDate;
    private BigDecimal costAmount;
    private String currency;
    private LocalDateTime updatedAt;

    public AzureDailyCost() {
    }

    public AzureDailyCost(Long id, LocalDate recordDate, BigDecimal costAmount, String currency, LocalDateTime updatedAt) {
        this.id = id;
        this.recordDate = recordDate;
        this.costAmount = costAmount;
        this.currency = currency;
        this.updatedAt = updatedAt;
    }

    private AzureDailyCost(Builder builder) {
        this.id = builder.id;
        this.recordDate = builder.recordDate;
        this.costAmount = builder.costAmount;
        this.currency = builder.currency;
        this.updatedAt = builder.updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(LocalDate recordDate) {
        this.recordDate = recordDate;
    }

    public BigDecimal getCostAmount() {
        return costAmount;
    }

    public void setCostAmount(BigDecimal costAmount) {
        this.costAmount = costAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static class Builder {
        private Long id;
        private LocalDate recordDate;
        private BigDecimal costAmount;
        private String currency;
        private LocalDateTime updatedAt;

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

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public AzureDailyCost build() {
            return new AzureDailyCost(this);
        }
    }
}
