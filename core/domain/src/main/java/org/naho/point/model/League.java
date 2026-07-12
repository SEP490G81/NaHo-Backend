package org.naho.point.model;

import org.naho.i18n.message.point.LeagueDetailMessageKey;
import org.naho.point.exception.LeagueDomainErrorCode;
import org.naho.point.type.LeagueName;
import org.naho.shared.exception.DomainException;

public class League {

    private final Long id;
    private final Long iconFileId;
    private final LeagueName name;
    private final String description;
    private final Double minPoint;
    private final Double maxPoint;

    private League(Builder builder) {
        this.id = builder.id;
        this.iconFileId = builder.iconFileId;
        this.name = builder.name;
        this.description = builder.description;
        this.minPoint = builder.minPoint;
        this.maxPoint = builder.maxPoint;
    }

    public Long getId() {
        return id;
    }

    public Long getIconFileId() {
        return iconFileId;
    }

    public LeagueName getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getMinPoint() {
        return minPoint;
    }

    public Double getMaxPoint() {
        return maxPoint;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private Long iconFileId;
        private LeagueName name;
        private String description;
        private Double minPoint;
        private Double maxPoint;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder iconFileId(Long iconFileId) {
            this.iconFileId = iconFileId;
            return this;
        }

        public Builder name(LeagueName name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder minPoint(Double minPoint) {
            this.minPoint = minPoint;
            return this;
        }

        public Builder maxPoint(Double maxPoint) {
            this.maxPoint = maxPoint;
            return this;
        }

        public League build() {
            if (iconFileId == null) {
                throw new DomainException(
                        LeagueDomainErrorCode.LEAGUE_ICON_FILE_EMPTY,
                        LeagueDetailMessageKey.LEAGUE_ICON_FILE_EMPTY
                );
            }

            if (name == null) {
                throw new DomainException(
                        LeagueDomainErrorCode.LEAGUE_NAME_EMPTY,
                        LeagueDetailMessageKey.LEAGUE_NAME_EMPTY
                );
            }

            if (minPoint == null) {
                throw new DomainException(
                        LeagueDomainErrorCode.LEAGUE_MIN_POINT_EMPTY,
                        LeagueDetailMessageKey.LEAGUE_MIN_POINT_EMPTY
                );
            }

            if (maxPoint == null) {
                throw new DomainException(
                        LeagueDomainErrorCode.LEAGUE_MAX_POINT_EMPTY,
                        LeagueDetailMessageKey.LEAGUE_MAX_POINT_EMPTY
                );
            }

            if (minPoint >= maxPoint) {
                throw new DomainException(
                        LeagueDomainErrorCode.LEAGUE_MIN_POINT_GREATER_THAN_OR_EQUAL_TO_MAX_POINT,
                        LeagueDetailMessageKey.LEAGUE_MIN_POINT_GREATER_THAN_OR_EQUAL_TO_MAX_POINT
                );
            }
            return new League(this);
        }
    }
}