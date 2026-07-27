package org.naho.daily.model;

import org.naho.daily.exception.UserDailyMissionDomainErrorCode;
import org.naho.i18n.message.daily.UserDailyMissionDetailMessageKey;
import org.naho.shared.exception.DomainException;

import java.time.Instant;

public class UserDailyMission {

    private Long id;
    private Long userId;
    private Long dailyMissionId;
    private Instant completedAt;

    private UserDailyMission(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.dailyMissionId = builder.dailyMissionId;
        this.completedAt = builder.completedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getDailyMissionId() {
        return dailyMissionId;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setDailyMissionId(Long dailyMissionId) {
        this.dailyMissionId = dailyMissionId;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Long id;
        private Long userId;
        private Long dailyMissionId;
        private Instant completedAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder dailyMissionId(Long dailyMissionId) {
            this.dailyMissionId = dailyMissionId;
            return this;
        }

        public Builder completedAt(Instant completedAt) {
            this.completedAt = completedAt;
            return this;
        }

        public UserDailyMission build() {
            if (userId == null) {
                throw new DomainException(
                        UserDailyMissionDomainErrorCode.USER_DAILY_MISSION_USER_ID_REQUIRED,
                        UserDailyMissionDetailMessageKey.USER_DAILY_MISSION_USER_ID_REQUIRED
                );
            }

            if (dailyMissionId == null) {
                throw new DomainException(
                        UserDailyMissionDomainErrorCode.USER_DAILY_MISSION_DAILY_MISSION_ID_REQUIRED,
                        UserDailyMissionDetailMessageKey.USER_DAILY_MISSION_DAILY_MISSION_ID_REQUIRED
                );
            }

            if (completedAt == null) {
                throw new DomainException(
                        UserDailyMissionDomainErrorCode.USER_DAILY_MISSION_COMPLETED_AT_REQUIRED,
                        UserDailyMissionDetailMessageKey.USER_DAILY_MISSION_COMPLETED_AT_REQUIRED
                );
            }

            return new UserDailyMission(this);
        }
    }
}
