package org.naho.initializer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.daily.port.in.CrudDailyMissionInputPort;
import org.naho.daily.repository.DailyMissionJpaRepository;
import org.naho.shared.constant.SystemZoneId;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Order(6)
@Component
@Slf4j
@RequiredArgsConstructor
public class DailyMissionInitializer implements ApplicationRunner {
    private final DailyMissionJpaRepository dailyMissionJpaRepository;
    private final CrudDailyMissionInputPort crudDailyMissionInputPort;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        LocalDate today = LocalDate.now(SystemZoneId.HO_CHI_MINH_ZONE_ID);

        if (dailyMissionJpaRepository.existsByMissionDate(today)) {
            log.info("DailyMission Data existed!");
        } else {
            log.info("Initializing DailyMission...");
            crudDailyMissionInputPort.createTodayMissions();
            log.info("DailyMission initialized!");
        }
    }
}
