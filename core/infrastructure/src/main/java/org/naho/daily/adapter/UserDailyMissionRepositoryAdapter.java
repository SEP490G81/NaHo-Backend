package org.naho.daily.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.daily.entity.UserDailyMissionEntity;
import org.naho.daily.mapper.UserDailyMissionEntityMapper;
import org.naho.daily.model.UserDailyMission;
import org.naho.daily.port.out.UserDailyMissionRepositoryPort;
import org.naho.daily.repository.UserDailyMissionJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserDailyMissionRepositoryAdapter implements UserDailyMissionRepositoryPort {

    private final UserDailyMissionJpaRepository userDailyMissionJpaRepository;
    private final UserDailyMissionEntityMapper userDailyMissionEntityMapper;

    @Override
    public boolean existsByUserIdAndDailyMissionId(Long userId, Long dailyMissionId) {
        return userDailyMissionJpaRepository.existsByUser_IdAndDailyMission_Id(userId, dailyMissionId);
    }

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
}
