package org.naho.daily.repository;

import org.naho.daily.entity.UserDailyAttendanceEntity;
import org.naho.shared.persistence.BaseJpaRepository;

import java.time.LocalDate;

public interface UserDailyAttendanceJpaRepository extends BaseJpaRepository<UserDailyAttendanceEntity> {
    boolean existsByIdAndUser_Id(Long id, Long userId);

    boolean existsByUser_IdAndAttendanceDate(Long userId, LocalDate attendanceDate);
}
