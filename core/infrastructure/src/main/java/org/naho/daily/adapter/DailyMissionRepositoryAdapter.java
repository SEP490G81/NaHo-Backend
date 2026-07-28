package org.naho.daily.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.daily.entity.DailyMissionEntity;
import org.naho.daily.mapper.DailyMissionEntityMapper;
import org.naho.daily.model.DailyMission;
import org.naho.daily.port.out.DailyMissionRepositoryPort;
import org.naho.daily.repository.DailyMissionJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DailyMissionRepositoryAdapter implements DailyMissionRepositoryPort {
    private final DailyMissionJpaRepository dailyMissionJpaRepository;
    private final DailyMissionEntityMapper dailyMissionEntityMapper;

    @Override
    public List<DailyMission> saveAll(List<DailyMission> dailyMissions) {
        List<DailyMissionEntity> entities = dailyMissions.stream()
                .map(dailyMissionEntityMapper::domainToEntity)
                .toList();
        return dailyMissionJpaRepository.saveAll(entities).stream()
                .map(dailyMissionEntityMapper::entityToDomain)
                .toList();
    }

    @Override
    public DailyMission save(DailyMission dailyMission) {
        DailyMissionEntity entity = dailyMissionEntityMapper.domainToEntity(dailyMission);
        DailyMissionEntity savedEntity = dailyMissionJpaRepository.save(entity);
        return dailyMissionEntityMapper.entityToDomain(savedEntity);
    }

    @Override
    public Optional<DailyMission> findById(Long id) {
        return dailyMissionJpaRepository
                .findById(id)
                .map(dailyMissionEntityMapper::entityToDomain);
    }

    @Override
    public List<DailyMission> findAll() {
        return dailyMissionJpaRepository
                .findAll()
                .stream().map(dailyMissionEntityMapper::entityToDomain)
                .toList();
    }
}
