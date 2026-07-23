package org.naho.daily.port.out;

import org.naho.daily.model.UserDailyAttendance;

import java.time.LocalDate;

public interface UserDailyAttendanceRepositoryPort {
    UserDailyAttendance save(UserDailyAttendance userDailyAttendance);

    boolean existsByIdAndUser_Id(Long id, Long userId);

    boolean existsByUser_IdAndAttendanceDate(Long userId, LocalDate attendanceDate);
}
