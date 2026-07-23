package org.naho.chest.result;

import org.naho.chest.type.ChestType;

public record ChestResult(
        Long id,
        ChestType chestType,
        String description,
        Integer minPoint,
        Integer maxPoint
) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private ChestType chestType;
        private String description;
        private Integer minPoint;
        private Integer maxPoint;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder chestType(ChestType chestType) {
            this.chestType = chestType;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder minPoint(Integer minPoint) {
            this.minPoint = minPoint;
            return this;
        }

        public Builder maxPoint(Integer maxPoint) {
            this.maxPoint = maxPoint;
            return this;
        }

        public ChestResult build() {
            return new ChestResult(
                    id,
                    chestType,
                    description,
                    minPoint,
                    maxPoint
            );
        }
    }
}