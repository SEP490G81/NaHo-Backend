package org.naho.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.cost.port.out.AwsDailyCostSyncServicePort;
import org.naho.shared.constant.SystemZoneId;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class AwsDailyCostScheduler {
    private final AwsDailyCostSyncServicePort awsDailyCostSyncServicePort;

    /**
     * Chạy mỗi 00:05 hàng ngày
     * Sync cost của 3 ngày gần nhất (không bao gồm ngày hôm nay)
     */
    @Scheduled(cron = "0 5 0 * * *", zone = SystemZoneId.HO_CHI_MINH_ZONE_ID_NAME)
    public void syncYesterdayCost() {
        LocalDate today = LocalDate.now(
                SystemZoneId.HO_CHI_MINH_ZONE_ID
        );

        // hôm kìa
        LocalDate from = today.minusDays(3);
        
        // hôm qua
        LocalDate to = today.minusDays(1);

        log.info("Starting AWS daily cost sync from {} to {}...", from, to);

        awsDailyCostSyncServicePort.sync(from, to);

        log.info("AWS daily cost sync from {} to {} completed!", from, to);
    }
}
