package org.naho.daily.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.daily.entity.UserDailyAttendanceEntity;
import org.naho.daily.mapper.UserDailyAttendanceEntityMapper;
import org.naho.daily.model.UserDailyAttendance;
import org.naho.daily.port.out.UserDailyAttendanceRepositoryPort;
import org.naho.daily.repository.UserDailyAttendanceJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class UserDailyAttendanceRepositoryAdapter implements UserDailyAttendanceRepositoryPort {

    private final UserDailyAttendanceEntityMapper userDailyAttendanceEntityMapper;
    private final UserDailyAttendanceJpaRepository userDailyAttendanceJpaRepository;

    @Override
    public UserDailyAttendance save(UserDailyAttendance userDailyAttendance) {
        UserDailyAttendanceEntity entity =
                userDailyAttendanceEntityMapper.domainToEntity(userDailyAttendance);

        UserDailyAttendanceEntity savedEntity = userDailyAttendanceJpaRepository.save(entity);

        return userDailyAttendanceEntityMapper.entityToDomain(savedEntity);
    }

    @Override
    public boolean existsByIdAndUser_Id(Long id, Long userId) {
        return userDailyAttendanceJpaRepository.existsByIdAndUser_Id(id, userId);
    }

    @Override
    public boolean existsByUser_IdAndAttendanceDate(Long userId, LocalDate attendanceDate) {
        return userDailyAttendanceJpaRepository.existsByUser_IdAndAttendanceDate(userId, attendanceDate);
    }
}
