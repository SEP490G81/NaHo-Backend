package org.naho.daily.model;

import org.naho.daily.exception.DailyMissionDomainErrorCode;
import org.naho.daily.type.MissionType;
import org.naho.i18n.message.daily.DailyMissionDetailMessageKey;
import org.naho.shared.exception.DomainException;

import java.time.LocalDate;

public class DailyMission {

    private final Long id;
    private final Long chestId;
    private final String title;

    private String description;
    private LocalDate missionDate;
    private MissionType missionType;

    private DailyMission(Builder builder) {
        this.id = builder.id;
        this.chestId = builder.chestId;
        this.title = builder.title;
        this.description = builder.description;
        this.missionDate = builder.missionDate;
        this.missionType = builder.missionType;
    }

    public Long getId() {
        return id;
    }

    public Long getChestId() {
        return chestId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getMissionDate() {
        return missionDate;
    }

    public MissionType getMissionType() {
        return missionType;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMissionDate(LocalDate missionDate) {
        this.missionDate = missionDate;
    }

    public void setMissionType(MissionType missionType) {
        this.missionType = missionType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Long id;
        private Long chestId;
        private String title;
        private String description;
        private LocalDate missionDate;
        private MissionType missionType;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder chestId(Long chestId) {
            this.chestId = chestId;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder missionDate(LocalDate missionDate) {
            this.missionDate = missionDate;
            return this;
        }

        public Builder missionType(MissionType missionType) {
            this.missionType = missionType;
            return this;
        }

        public DailyMission build() {
            if (chestId == null) {
                throw new DomainException(
                        DailyMissionDomainErrorCode.DAILY_MISSION_CHEST_ID_REQUIRED,
                        DailyMissionDetailMessageKey.DAILY_MISSION_CHEST_ID_REQUIRED
                );
            }

            if (title == null || title.isBlank()) {
                throw new DomainException(
                        DailyMissionDomainErrorCode.DAILY_MISSION_TITLE_REQUIRED,
                        DailyMissionDetailMessageKey.DAILY_MISSION_TITLE_REQUIRED
                );
            }

            if (missionDate == null) {
                throw new DomainException(
                        DailyMissionDomainErrorCode.DAILY_MISSION_DATE_REQUIRED,
                        DailyMissionDetailMessageKey.DAILY_MISSION_DATE_REQUIRED
                );
            }

            if (missionType == null) {
                throw new DomainException(
                        DailyMissionDomainErrorCode.DAILY_MISSION_TYPE_REQUIRED,
                        DailyMissionDetailMessageKey.DAILY_MISSION_TYPE_REQUIRED
                );
            }

            return new DailyMission(this);
        }
    }
}
