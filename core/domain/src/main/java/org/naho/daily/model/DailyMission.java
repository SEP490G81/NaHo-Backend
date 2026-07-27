package org.naho.daily.model;

import org.naho.daily.exception.DailyMissionDomainErrorCode;
import org.naho.daily.type.MissionType;
import org.naho.i18n.message.daily.DailyMissionDetailMessageKey;
import org.naho.shared.exception.DomainException;

import java.time.LocalDate;

public class DailyMission {

    private final Long id;
    private Double point;
    private LocalDate missionDate;
    private MissionType missionType;

    private DailyMission(Builder builder) {
        this.id = builder.id;
        this.point = builder.point;
        this.missionDate = builder.missionDate;
        this.missionType = builder.missionType;
    }

    public Long getId() {
        return id;
    }

    public Double getPoint() {
        return point;
    }

    public LocalDate getMissionDate() {
        return missionDate;
    }

    public MissionType getMissionType() {
        return missionType;
    }

    public void setPoint(Double point) {
        this.point = point;
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
        private Double point;
        private LocalDate missionDate;
        private MissionType missionType;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder point(Double point) {
            this.point = point;
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
            if (point == null) {
                throw new DomainException(
                        DailyMissionDomainErrorCode.DAILY_MISSION_POINT_REQUIRED,
                        DailyMissionDetailMessageKey.DAILY_MISSION_POINT_REQUIRED
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


