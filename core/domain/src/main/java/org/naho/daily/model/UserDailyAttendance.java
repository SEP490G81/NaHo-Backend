package org.naho.daily.model;

import org.naho.daily.exception.UserDailyAttendanceDomainErrorCode;
import org.naho.i18n.message.daily.UserDailyAttendanceDetailMessageKey;
import org.naho.shared.exception.DomainException;

import java.time.LocalDate;

public class UserDailyAttendance {

    private final Long id;
    private final Long userId;
    private final Long dailyRewardId;

    private final LocalDate attendanceDate;

    private UserDailyAttendance(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.dailyRewardId = builder.dailyRewardId;
        this.attendanceDate = builder.attendanceDate;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getDailyRewardId() {
        return dailyRewardId;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Long id;
        private Long userId;
        private Long dailyRewardId;
        private LocalDate attendanceDate;

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

        public Builder dailyRewardId(Long dailyRewardId) {
            this.dailyRewardId = dailyRewardId;
            return this;
        }

        public Builder attendanceDate(LocalDate attendanceDate) {
            this.attendanceDate = attendanceDate;
            return this;
        }

        public UserDailyAttendance build() {
            if (userId == null) {
                throw new DomainException(
                        UserDailyAttendanceDomainErrorCode.USER_DAILY_ATTENDANCE_USER_ID_REQUIRED,
                        UserDailyAttendanceDetailMessageKey.USER_DAILY_ATTENDANCE_USER_ID_REQUIRED
                );
            }

            if (dailyRewardId == null) {
                throw new DomainException(
                        UserDailyAttendanceDomainErrorCode.USER_DAILY_ATTENDANCE_DAILY_REWARD_ID_REQUIRED,
                        UserDailyAttendanceDetailMessageKey.USER_DAILY_ATTENDANCE_DAILY_REWARD_ID_REQUIRED
                );
            }

            if (attendanceDate == null) {
                throw new DomainException(
                        UserDailyAttendanceDomainErrorCode.USER_DAILY_ATTENDANCE_DATE_REQUIRED,
                        UserDailyAttendanceDetailMessageKey.USER_DAILY_ATTENDANCE_DATE_REQUIRED
                );
            }

            if (attendanceDate.isAfter(LocalDate.now())) {
                throw new DomainException(
                        UserDailyAttendanceDomainErrorCode.USER_DAILY_ATTENDANCE_DATE_FUTURE,
                        UserDailyAttendanceDetailMessageKey.USER_DAILY_ATTENDANCE_DATE_FUTURE
                );
            }

            return new UserDailyAttendance(this);
        }
    }
}
