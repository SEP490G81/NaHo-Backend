package org.naho.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.cost.port.in.SyncOpenAiCostInputPort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiCostScheduler {
    private final SyncOpenAiCostInputPort syncOpenAiCostInputPort;

    @Scheduled(cron = "0 0 */4 * * *")
    public void scheduledIncrementalSync() {
        log.info("Running scheduled incremental sync for OpenAI daily costs (cron)...");
        try {
            syncOpenAiCostInputPort.syncIncremental(3);
        } catch (Exception e) {
            log.error("Error during scheduled OpenAI cost incremental sync: ", e);
        }
    }
}
