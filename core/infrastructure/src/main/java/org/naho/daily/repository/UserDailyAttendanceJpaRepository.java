package org.naho.daily.repository;

import org.naho.daily.entity.UserDailyAttendanceEntity;
import org.naho.shared.persistence.BaseJpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface UserDailyAttendanceJpaRepository extends BaseJpaRepository<UserDailyAttendanceEntity> {
    boolean existsByIdAndUser_Id(Long id, Long userId);

    boolean existsByUser_IdAndAttendanceDate(Long userId, LocalDate attendanceDate);

    List<UserDailyAttendanceEntity> findAllByUser_IdAndAttendanceDateBetween(Long userId, LocalDate attendanceDateAfter, LocalDate attendanceDateBefore);
}
