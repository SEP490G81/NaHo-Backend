package org.naho.initializer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.cost.port.in.SyncAzureCostInputPort;
import org.naho.cost.repository.AzureDailyCostJpaRepository;
import org.naho.shared.constant.SystemZoneId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Order(8)
@Slf4j
@Component
@RequiredArgsConstructor
public class AzureDailyCostInitializer8 implements ApplicationRunner {
    private final SyncAzureCostInputPort syncAzureCostInputPort;
    private final AzureDailyCostJpaRepository azureDailyCostJpaRepository;

    @Value("${app.start-date}")
    private LocalDate appStartDate;

    @Override
    public void run(ApplicationArguments args) {
        try {
            LocalDate to = LocalDate.now(SystemZoneId.HO_CHI_MINH_ZONE_ID);
            LocalDate lastRecordDate = azureDailyCostJpaRepository.findMaxRecordDate();
            LocalDate from = lastRecordDate != null ? lastRecordDate.minusDays(2) : appStartDate;

            log.info("Initializing Azure daily cost from {} to {}...", from, to);
            syncAzureCostInputPort.syncCustomRange(from, to);
            log.info("Azure daily cost to {} initialized!", to);
        } catch (Throwable t) {
            log.error("Failed to initialize Azure daily cost on startup (non-fatal): ", t);
        }
    }
}
