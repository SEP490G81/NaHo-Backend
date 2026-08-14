package org.naho.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.cost.port.in.SyncAzureCostInputPort;
import org.naho.shared.constant.SystemZoneId;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AzureCostScheduler {

    private final SyncAzureCostInputPort syncAzureCostInputPort;

    /**
     * Chạy mỗi 00:05 hàng ngày
     * Sync cost của 3 ngày gần nhất (không bao gồm ngày hôm nay)
     */
    @Scheduled(cron = "0 5 0 * * *", zone = SystemZoneId.HO_CHI_MINH_ZONE_ID_NAME)
    public void scheduledIncrementalSync() {
        log.info("Running scheduled incremental sync for Azure daily costs (cron)...");
        try {
            syncAzureCostInputPort.syncIncremental(3);
        } catch (Exception e) {
            log.error("Error during scheduled Azure cost incremental sync: ", e);
        }
    }
}
