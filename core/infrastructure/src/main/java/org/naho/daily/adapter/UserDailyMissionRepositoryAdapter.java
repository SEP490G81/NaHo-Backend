package org.naho.daily.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.daily.entity.UserDailyMissionEntity;
import org.naho.daily.mapper.UserDailyMissionEntityMapper;
import org.naho.daily.model.UserDailyMission;
import org.naho.daily.port.out.UserDailyMissionRepositoryPort;
import org.naho.daily.repository.UserDailyMissionJpaRepository;
import org.naho.daily.type.MissionStatus;
import org.naho.daily.type.MissionType;
import org.naho.shared.exception.InfrastructureException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserDailyMissionRepositoryAdapter implements UserDailyMissionRepositoryPort {

    private final UserDailyMissionJpaRepository userDailyMissionJpaRepository;
    private final UserDailyMissionEntityMapper userDailyMissionEntityMapper;

    @Override
    public UserDailyMission save(UserDailyMission userDailyMission) {
        UserDailyMissionEntity entity = userDailyMissionEntityMapper.domainToEntity(userDailyMission);
        UserDailyMissionEntity savedEntity = userDailyMissionJpaRepository.save(entity);
        return userDailyMissionEntityMapper.entityToDomain(savedEntity);
    }

    @Override
    public List<UserDailyMission> findAllByUserId(Long userId) {
        return userDailyMissionJpaRepository
                .findAllByUser_Id(userId)
                .stream().map(userDailyMissionEntityMapper::entityToDomain)
                .toList();
    }

    @Override
    public List<UserDailyMission> findAllByUser_IdAndStartedDate(Long userId, LocalDate startedDate) {
        return userDailyMissionJpaRepository
                .findAllByUser_IdAndStartedDate(userId, startedDate)
                .stream().map(userDailyMissionEntityMapper::entityToDomain)
                .toList();
    }

    @Override
    public Optional<UserDailyMission> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return userDailyMissionJpaRepository
                .findById(id)
                .map(userDailyMissionEntityMapper::entityToDomain);
    }

    @Override
    public Optional<UserDailyMission> findByIdAndUserId(Long id, Long userId) {
        if (id == null || userId == null) {
            return Optional.empty();
        }

        return userDailyMissionJpaRepository
                .findByIdAndUser_Id(id, userId)
                .map(userDailyMissionEntityMapper::entityToDomain);
    }

    @Override
    public Optional<UserDailyMission> findByUserIdAndDailyMissionMissionTypeAndStartedDate(Long userId,
            MissionType missionType, LocalDate startedDate) {
        if (userId == null || missionType == null || startedDate == null) {
            return Optional.empty();
        }

        return userDailyMissionJpaRepository
                .findByUser_IdAndDailyMission_MissionTypeAndStartedDate(userId, missionType, startedDate)
                .map(userDailyMissionEntityMapper::entityToDomain);
    }
}
