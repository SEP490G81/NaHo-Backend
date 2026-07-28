package org.naho.daily.usecase;

import org.naho.daily.mapper.DailyMissionResultMapper;
import org.naho.daily.port.in.CrudDailyMissionInputPort;
import org.naho.daily.port.out.DailyMissionRepositoryPort;
import org.naho.daily.result.DailyMissionResult;

import java.util.Optional;

public class CrudDailyMissionUseCase implements CrudDailyMissionInputPort {
    private final DailyMissionRepositoryPort dailyMissionRepositoryPort;
    private final DailyMissionResultMapper dailyMissionResultMapper;

    public CrudDailyMissionUseCase(
            DailyMissionRepositoryPort dailyMissionRepositoryPort,
            DailyMissionResultMapper dailyMissionResultMapper
    ) {
        this.dailyMissionRepositoryPort = dailyMissionRepositoryPort;
        this.dailyMissionResultMapper = dailyMissionResultMapper;
    }

    @Override
    public Optional<DailyMissionResult> findById(Long id) {
        return dailyMissionRepositoryPort
                .findById(id)
                .map(dailyMissionResultMapper::domainToResult);
    }
}
