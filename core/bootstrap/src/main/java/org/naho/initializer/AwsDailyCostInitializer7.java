package org.naho.initializer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.cost.port.out.AwsDailyCostSyncServicePort;
import org.naho.cost.repository.AwsDailyCostJpaRepository;
import org.naho.shared.constant.SystemZoneId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Order(7)
@Slf4j
@Component
@RequiredArgsConstructor
public class AwsDailyCostInitializer7 implements ApplicationRunner {
    private final AwsDailyCostSyncServicePort awsDailyCostSyncServicePort;
    private final AwsDailyCostJpaRepository awsDailyCostJpaRepository;

    @Value("${app.start-date}")
    private LocalDate appStartDate;

    /**
     * Nếu chưa có trong db thì sync toàn bộ từ ngày `appStartDate`
     * Nếu có rồi thì lấy 3 ngày gần nhất để sync (hôm nay, hôm qua, và hôm kia)
     *
     * @param args incoming application arguments
     * @throws Exception exception
     */
    @Override
    public void run(ApplicationArguments args) {
        try {
            LocalDate to = LocalDate.now(SystemZoneId.HO_CHI_MINH_ZONE_ID);
            LocalDate lastRecordDate = awsDailyCostJpaRepository.findMaxRecordDate();
            LocalDate from = lastRecordDate != null ? lastRecordDate.minusDays(2) : appStartDate;

            log.info("Initializing AWS daily cost to {}...", to);
            awsDailyCostSyncServicePort.sync(from, to);
            log.info("AWS daily cost to {} initialized!", to);
        } catch (Throwable t) {
            log.error("Failed to initialize AWS daily cost on startup (non-fatal): ", t);
        }
    }
}
