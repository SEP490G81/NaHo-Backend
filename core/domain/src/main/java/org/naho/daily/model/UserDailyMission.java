package org.naho.daily.model;

import org.naho.daily.exception.UserDailyMissionDomainErrorCode;
import org.naho.daily.type.MissionStatus;
import org.naho.i18n.message.daily.UserDailyMissionDetailMessageKey;
import org.naho.shared.exception.DomainException;

import java.time.LocalDate;

public class UserDailyMission {

    private Long id;
    private Long userId;
    private Long dailyMissionId;
    private MissionStatus status;
    private LocalDate startedDate;
    private LocalDate completedDate;
    private LocalDate earnedDate;

    private UserDailyMission(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.dailyMissionId = builder.dailyMissionId;
        this.status = builder.status;
        this.startedDate = builder.startedDate;
        this.completedDate = builder.completedDate;
        this.earnedDate = builder.earnedDate;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDailyMissionId() {
        return dailyMissionId;
    }

    public void setDailyMissionId(Long dailyMissionId) {
        this.dailyMissionId = dailyMissionId;
    }

    public MissionStatus getStatus() {
        return status;
    }

    public void setStatus(MissionStatus status) {
        this.status = status;
    }

    public LocalDate getStartedDate() {
        return startedDate;
    }

    public void setStartedDate(LocalDate startedDate) {
        this.startedDate = startedDate;
    }

    public LocalDate getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(LocalDate completedDate) {
        this.completedDate = completedDate;
    }

    public LocalDate getEarnedDate() {
        return earnedDate;
    }

    public void setEarnedDate(LocalDate earnedDate) {
        this.earnedDate = earnedDate;
    }

    public static class Builder {

        private Long id;
        private Long userId;
        private Long dailyMissionId;
        private MissionStatus status;
        private LocalDate startedDate;
        private LocalDate completedDate;
        private LocalDate earnedDate;

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

        public Builder startedDate(LocalDate startedDate) {
            this.startedDate = startedDate;
            return this;
        }

        public Builder completedDate(LocalDate completedDate) {
            this.completedDate = completedDate;
            return this;
        }

        public Builder earnedDate(LocalDate earnedDate) {
            this.earnedDate = earnedDate;
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

            return new UserDailyMission(this);
        }
    }
}
