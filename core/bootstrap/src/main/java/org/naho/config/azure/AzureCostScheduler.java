package org.naho.config.azure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.speech.azure.port.in.SyncAzureCostInputPort;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AzureCostScheduler implements ApplicationRunner {

    private final SyncAzureCostInputPort syncAzureCostInputPort;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Checking Azure cost history initial backfill state on startup...");
        try {
            syncAzureCostInputPort.syncInitialBackfillIfEmpty();
        } catch (Exception e) {
            log.error("Failed to run initial Azure cost backfill on startup: ", e);
        }
    }

    @Scheduled(cron = "0 0 */4 * * *")
    public void scheduledIncrementalSync() {
        log.info("Running scheduled incremental sync for Azure daily costs (cron)...");
        try {
            syncAzureCostInputPort.syncIncremental(3);
        } catch (Exception e) {
            log.error("Error during scheduled Azure cost incremental sync: ", e);
        }
    }
}
