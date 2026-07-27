package org.naho.daily.usecase;

import org.naho.daily.port.in.CrudDailyMissionInputPort;
import org.naho.daily.port.out.DailyMissionRepositoryPort;
import org.naho.shared.constant.SystemZoneId;

import java.time.LocalDate;

public class CrudDailyMissionUseCase implements CrudDailyMissionInputPort {
    private final DailyMissionRepositoryPort dailyMissionRepositoryPort;

    public CrudDailyMissionUseCase(
            DailyMissionRepositoryPort dailyMissionRepositoryPort
    ) {
        this.dailyMissionRepositoryPort = dailyMissionRepositoryPort;
    }

    @Override
    public void createTodayMissions() {
        LocalDate today = LocalDate.now(SystemZoneId.HO_CHI_MINH_ZONE_ID);
        if (dailyMissionRepositoryPort.existsByMissionDate(today)) {
            return;
        }
    }
}
