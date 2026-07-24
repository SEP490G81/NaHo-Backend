package org.naho.daily.result;

import java.time.LocalDate;

public record UserDailyAttendanceResult(
        Long id,
        Long userId,
        Long dailyRewardId,
        LocalDate attendanceDate,
        Integer earnedPoint
) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long userId;
        private Long dailyRewardId;
        private LocalDate attendanceDate;
        private Integer earnedPoint;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder dailyRewardId(Long dailyRewardId) {
            this.dailyRewardId = dailyRewardId;
            return this;
        }

        public Builder attendanceDate(LocalDate attendanceDate) {
            this.attendanceDate = attendanceDate;
            return this;
        }

        public Builder earnedPoint(Integer earnedPoint) {
            this.earnedPoint = earnedPoint;
            return this;
        }

        public UserDailyAttendanceResult build() {
            return new UserDailyAttendanceResult(
                    id,
                    userId,
                    dailyRewardId,
                    attendanceDate,
                    earnedPoint
            );
        }
    }
}