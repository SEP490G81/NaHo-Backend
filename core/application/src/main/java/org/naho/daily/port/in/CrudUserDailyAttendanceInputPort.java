package org.naho.daily.port.in;

import org.naho.daily.result.UserDailyAttendanceResult;

import java.util.List;

public interface CrudUserDailyAttendanceInputPort {
    List<UserDailyAttendanceResult> findAllUserDailyAttendanceOfCurrentMonth(Long userId);
}
