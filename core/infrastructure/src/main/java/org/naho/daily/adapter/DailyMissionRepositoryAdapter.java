package org.naho.daily.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.daily.exception.DailyMissionDomainErrorCode;
import org.naho.daily.model.DailyMission;
import org.naho.daily.port.out.DailyMissionRepositoryPort;
import org.naho.daily.repository.DailyMissionJpaRepository;
import org.naho.i18n.message.daily.DailyMissionDetailMessageKey;
import org.naho.shared.exception.InfrastructureException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DailyMissionRepositoryAdapter implements DailyMissionRepositoryPort {
    private final DailyMissionJpaRepository dailyMissionJpaRepository;

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
}
