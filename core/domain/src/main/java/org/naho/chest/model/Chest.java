package org.naho.chest.model;

import org.naho.chest.exception.ChestDomainErrorCode;
import org.naho.chest.type.ChestType;
import org.naho.i18n.message.chest.ChestDetailMessageKey;
import org.naho.shared.exception.DomainException;

import java.util.concurrent.ThreadLocalRandom;

public class Chest {

    private final Long id;
    private ChestType chestType;
    private String description;
    private Integer minPoint;
    private Integer maxPoint;

    private Chest(Builder builder) {
        this.id = builder.id;
        this.chestType = builder.chestType;
        this.description = builder.description;
        this.minPoint = builder.minPoint;
        this.maxPoint = builder.maxPoint;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public ChestType getChestType() {
        return chestType;
    }

    public void setChestType(ChestType chestType) {
        this.chestType = chestType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMinPoint() {
        return minPoint;
    }

    public void setMinPoint(Integer minPoint) {
        this.minPoint = minPoint;
    }

    public Integer getMaxPoint() {
        return maxPoint;
    }

    public void setMaxPoint(Integer maxPoint) {
        this.maxPoint = maxPoint;
    }

    public int getRandomPoint() {
        return ThreadLocalRandom.current().nextInt(minPoint, maxPoint + 1);
    }

    public static final class Builder {

        private Long id;
        private ChestType chestType;
        private String description;
        private Integer minPoint;
        private Integer maxPoint;

        private Builder() {
        }

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

        public Chest build() {
            if (chestType == null) {
                throw new DomainException(
                        ChestDomainErrorCode.CHEST_TYPE_EMPTY,
                        ChestDetailMessageKey.CHEST_TYPE_EMPTY
                );
            }

            if (minPoint == null || minPoint < 0 || maxPoint == null || maxPoint < minPoint) {
                throw new DomainException(
                        ChestDomainErrorCode.CHEST_POINT_INVALID,
                        ChestDetailMessageKey.CHEST_POINT_INVALID
                );
            }
            return new Chest(this);
        }
    }
}
