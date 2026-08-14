package org.naho.config.openai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.speech.llm.port.in.SyncOpenAiCostInputPort;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiCostScheduler implements ApplicationRunner {

    private final SyncOpenAiCostInputPort syncOpenAiCostInputPort;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Checking OpenAI cost history initial backfill state on startup...");
        try {
            syncOpenAiCostInputPort.syncInitialBackfillIfEmpty();
        } catch (Exception e) {
            log.error("Failed to run initial OpenAI cost backfill on startup: ", e);
        }
    }

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
