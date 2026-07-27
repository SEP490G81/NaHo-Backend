package org.naho.daily.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.daily.entity.DailyMissionEntity;
import org.naho.daily.exception.DailyMissionDomainErrorCode;
import org.naho.daily.mapper.DailyMissionEntityMapper;
import org.naho.daily.model.DailyMission;
import org.naho.daily.port.out.DailyMissionRepositoryPort;
import org.naho.daily.repository.DailyMissionJpaRepository;
import org.naho.daily.type.MissionType;
import org.naho.i18n.message.daily.DailyMissionDetailMessageKey;
import org.naho.shared.exception.InfrastructureException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DailyMissionRepositoryAdapter implements DailyMissionRepositoryPort {
    private final DailyMissionJpaRepository dailyMissionJpaRepository;
    private final DailyMissionEntityMapper dailyMissionEntityMapper;

    @Override
    public List<DailyMission> saveAll(List<DailyMission> dailyMissions) {
        return List.of();
    }

    @Override
    public boolean existsByMissionDate(LocalDate missionDate) {
        if (missionDate == null) {
            throw new InfrastructureException(
                    DailyMissionDomainErrorCode.DAILY_MISSION_DATE_REQUIRED,
                    DailyMissionDetailMessageKey.DAILY_MISSION_DATE_REQUIRED
            );
        }
        return dailyMissionJpaRepository.existsByMissionDate(missionDate);
    }

    @Override
    public DailyMission save(DailyMission dailyMission) {
        DailyMissionEntity entity = dailyMissionEntityMapper.domainToEntity(dailyMission);
        DailyMissionEntity savedEntity = dailyMissionJpaRepository.save(entity);
        return dailyMissionEntityMapper.entityToDomain(savedEntity);
    }

    @Override
    public Optional<DailyMission> findByMissionDateAndMissionType(LocalDate missionDate, MissionType missionType) {
        return dailyMissionJpaRepository
                .findByMissionDateAndMissionType(missionDate, missionType)
                .map(dailyMissionEntityMapper::entityToDomain);
    }

    @Override
    public List<DailyMission> findAllByMissionDate(LocalDate missionDate) {
        return dailyMissionJpaRepository
                .findAllByMissionDate(missionDate)
                .stream().map(dailyMissionEntityMapper::entityToDomain)
                .toList();
    }
}
