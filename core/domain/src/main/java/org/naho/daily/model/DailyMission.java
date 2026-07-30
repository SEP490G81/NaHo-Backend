package org.naho.daily.model;

import org.naho.daily.exception.DailyMissionDomainErrorCode;
import org.naho.daily.type.MissionType;
import org.naho.i18n.message.daily.DailyMissionDetailMessageKey;
import org.naho.shared.exception.DomainException;

public class DailyMission {

    private final Long id;
    private String title;
    private String description;
    private Double point;
    private MissionType missionType;

    private DailyMission(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.description = builder.description;
        this.point = builder.point;
        this.missionType = builder.missionType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPoint() {
        return point;
    }

    public void setPoint(Double point) {
        this.point = point;
    }

    public MissionType getMissionType() {
        return missionType;
    }

    public void setMissionType(MissionType missionType) {
        this.missionType = missionType;
    }

    public static class Builder {

        private Long id;
        private String title;
        private String description;
        private Double point;
        private MissionType missionType;

        public Builder id(Long id) {
            this.id = id;
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

        public Builder point(Double point) {
            this.point = point;
            return this;
        }

        public Builder missionType(MissionType missionType) {
            this.missionType = missionType;
            return this;
        }

        public DailyMission build() {
            if (point == null) {
                throw new DomainException(
                        DailyMissionDomainErrorCode.DAILY_MISSION_POINT_REQUIRED,
                        DailyMissionDetailMessageKey.DAILY_MISSION_POINT_REQUIRED
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
