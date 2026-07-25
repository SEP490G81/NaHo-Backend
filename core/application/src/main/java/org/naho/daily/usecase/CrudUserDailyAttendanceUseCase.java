package org.naho.daily.usecase;

import org.naho.daily.mapper.UserDailyAttendanceResultMapper;
import org.naho.daily.model.UserDailyAttendance;
import org.naho.daily.port.in.CrudUserDailyAttendanceInputPort;
import org.naho.daily.port.out.UserDailyAttendanceRepositoryPort;
import org.naho.daily.result.UserDailyAttendanceResult;
import org.naho.shared.constant.SystemZoneId;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

public class CrudUserDailyAttendanceUseCase implements CrudUserDailyAttendanceInputPort {
    private final UserDailyAttendanceRepositoryPort userDailyAttendanceRepositoryPort;
    private final UserDailyAttendanceResultMapper userDailyAttendanceResultMapper;

    public CrudUserDailyAttendanceUseCase(
            UserDailyAttendanceRepositoryPort userDailyAttendanceRepositoryPort,
            UserDailyAttendanceResultMapper userDailyAttendanceResultMapper
    ) {
        this.userDailyAttendanceRepositoryPort = userDailyAttendanceRepositoryPort;
        this.userDailyAttendanceResultMapper = userDailyAttendanceResultMapper;
    }

    @Override
    public List<UserDailyAttendanceResult> findAllUserDailyAttendanceOfCurrentMonth(Long userId) {
        ZoneId zoneId = SystemZoneId.HO_CHI_MINH_ZONE_ID;

        LocalDate today = LocalDate.now(zoneId);
        YearMonth currentYearMonth = YearMonth.from(today);

        LocalDate firstDayOfMonth = currentYearMonth.atDay(1);
        LocalDate lastDayOfMonth = currentYearMonth.atEndOfMonth();

        List<UserDailyAttendance> userDailyAttendanceList =
                userDailyAttendanceRepositoryPort.findAllByUser_IdAndAttendanceDateBetween(
                        userId,
                        firstDayOfMonth,
                        lastDayOfMonth
                );

        return userDailyAttendanceList.stream()
                .map(userDailyAttendanceResultMapper::domainToResult)
                .toList();
    }
}
