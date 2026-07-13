package org.naho.point.model;

import org.naho.i18n.message.point.UserSeasonPointDetailMessageKey;
import org.naho.point.exception.UserSeasonPointDomainErrorCode;
import org.naho.shared.exception.DomainException;

public class UserSeasonPoint {

    private final Long id;
    private final Long userId;
    private final Long seasonId;
    private final Long leagueId;
    private final Double seasonPoint;

    private UserSeasonPoint(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.seasonId = builder.seasonId;
        this.leagueId = builder.leagueId;
        this.seasonPoint = builder.seasonPoint;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getSeasonId() {
        return seasonId;
    }

    public Long getLeagueId() {
        return leagueId;
    }

    public Double getSeasonPoint() {
        return seasonPoint;
    }

    public static final class Builder {
        private Long id;
        private Long userId;
        private Long seasonId;
        private Long leagueId;
        private Double seasonPoint;

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

        public Builder seasonId(Long seasonId) {
            this.seasonId = seasonId;
            return this;
        }

        public Builder leagueId(Long leagueId) {
            this.leagueId = leagueId;
            return this;
        }

        public Builder seasonPoint(Double seasonPoint) {
            this.seasonPoint = seasonPoint;
            return this;
        }

        public UserSeasonPoint build() {
            if (userId == null) {
                throw new DomainException(
                        UserSeasonPointDomainErrorCode.USER_SEASON_POINT_USER_EMPTY,
                        UserSeasonPointDetailMessageKey.USER_SEASON_POINT_USER_EMPTY
                );
            }

            if (seasonId == null) {
                throw new DomainException(
                        UserSeasonPointDomainErrorCode.USER_SEASON_POINT_SEASON_EMPTY,
                        UserSeasonPointDetailMessageKey.USER_SEASON_POINT_SEASON_EMPTY
                );
            }

            if (leagueId == null) {
                throw new DomainException(
                        UserSeasonPointDomainErrorCode.USER_SEASON_POINT_LEAGUE_EMPTY,
                        UserSeasonPointDetailMessageKey.USER_SEASON_POINT_LEAGUE_EMPTY
                );
            }

            if (seasonPoint == null) {
                throw new DomainException(
                        UserSeasonPointDomainErrorCode.USER_SEASON_POINT_SEASON_POINT_EMPTY,
                        UserSeasonPointDetailMessageKey.USER_SEASON_POINT_SEASON_POINT_EMPTY
                );
            }
            return new UserSeasonPoint(this);
        }
    }
}