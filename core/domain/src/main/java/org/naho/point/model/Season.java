package org.naho.point.model;

import org.naho.i18n.message.point.SeasonDetailMessageKey;
import org.naho.point.exception.SeasonDomainErrorCode;
import org.naho.shared.exception.DomainException;

import java.time.Instant;

public class Season {

    private final Long id;
    private final Integer seasonNo;
    private final Instant startAt;
    private final Instant endAt;

    private Season(Builder builder) {
        this.id = builder.id;
        this.seasonNo = builder.seasonNo;
        this.startAt = builder.startAt;
        this.endAt = builder.endAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public Integer getSeasonNo() {
        return seasonNo;
    }

    public Instant getStartAt() {
        return startAt;
    }

    public Instant getEndAt() {
        return endAt;
    }

    public static final class Builder {
        private Long id;
        private Integer seasonNo;
        private Instant startAt;
        private Instant endAt;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder seasonNo(Integer seasonNo) {
            this.seasonNo = seasonNo;
            return this;
        }

        public Builder startAt(Instant startAt) {
            this.startAt = startAt;
            return this;
        }

        public Builder endAt(Instant endAt) {
            this.endAt = endAt;
            return this;
        }

        public Season build() {
            if (seasonNo == null) {
                throw new DomainException(
                        SeasonDomainErrorCode.SEASON_NO_EMPTY,
                        SeasonDetailMessageKey.SEASON_NO_EMPTY
                );
            }

            if (startAt == null) {
                throw new DomainException(
                        SeasonDomainErrorCode.SEASON_START_AT_EMPTY,
                        SeasonDetailMessageKey.SEASON_START_AT_EMPTY
                );
            }

            if (endAt == null) {
                throw new DomainException(
                        SeasonDomainErrorCode.SEASON_END_AT_EMPTY,
                        SeasonDetailMessageKey.SEASON_END_AT_EMPTY
                );
            }

            if (!endAt.isAfter(startAt)) {
                throw new DomainException(
                        SeasonDomainErrorCode.SEASON_END_AT_BEFORE_OR_EQUAL_TO_START_AT,
                        SeasonDetailMessageKey.SEASON_END_AT_BEFORE_OR_EQUAL_TO_START_AT
                );
            }
            return new Season(this);
        }
    }
}