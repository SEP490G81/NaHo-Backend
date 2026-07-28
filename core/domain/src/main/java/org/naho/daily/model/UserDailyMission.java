package org.naho.daily.model;

import org.naho.daily.exception.UserDailyMissionDomainErrorCode;
import org.naho.daily.type.MissionStatus;
import org.naho.i18n.message.daily.UserDailyMissionDetailMessageKey;
import org.naho.shared.exception.DomainException;

import java.time.Instant;

public class UserDailyMission {

    private Long id;
    private Long userId;
    private Long dailyMissionId;
    private MissionStatus status;
    private Instant completedAt;
    private Instant earnedAt;

    private UserDailyMission(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.dailyMissionId = builder.dailyMissionId;
        this.status = builder.status;
        this.completedAt = builder.completedAt;
        this.earnedAt = builder.earnedAt;
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

    public MissionStatus getStatus() {
        return status;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public Instant getEarnedAt() {
        return earnedAt;
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

    public void setStatus(MissionStatus status) {
        this.status = status;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public void setEarnedAt(Instant earnedAt) {
        this.earnedAt = earnedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Long id;
        private Long userId;
        private Long dailyMissionId;
        private MissionStatus status;
        private Instant completedAt;
        private Instant earnedAt;

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

        public Builder status(MissionStatus status) {
            this.status = status;
            return this;
        }

        public Builder completedAt(Instant completedAt) {
            this.completedAt = completedAt;
            return this;
        }

        public Builder earnedAt(Instant earnedAt) {
            this.earnedAt = earnedAt;
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
