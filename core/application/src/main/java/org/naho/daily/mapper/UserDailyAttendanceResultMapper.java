package org.naho.daily.mapper;

import org.naho.daily.model.UserDailyAttendance;
import org.naho.daily.result.UserDailyAttendanceResult;

public class UserDailyAttendanceResultMapper {
    public UserDailyAttendanceResult domainToResult(UserDailyAttendance domain) {
        return UserDailyAttendanceResult.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .dailyRewardId(domain.getDailyRewardId())
                .attendanceDate(domain.getAttendanceDate())
                .build();
    }

    public UserDailyAttendanceResult domainToResult(UserDailyAttendance domain, Integer earnedPoint) {
        return UserDailyAttendanceResult.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .dailyRewardId(domain.getDailyRewardId())
                .attendanceDate(domain.getAttendanceDate())
                .earnedPoint(earnedPoint)
                .build();
    }
}
