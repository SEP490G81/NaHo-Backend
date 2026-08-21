package org.naho.initializer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.cost.port.in.SyncOpenAiCostInputPort;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(9)
@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiCostInitializer9 implements ApplicationRunner {
    private final SyncOpenAiCostInputPort syncOpenAiCostInputPort;

    @Override
    public void run(ApplicationArguments args) {
        try {
            log.info("Checking OpenAI cost history initial backfill state on startup...");
            syncOpenAiCostInputPort.syncInitialBackfillIfEmpty();
        } catch (Throwable t) {
            log.error("Failed to run initial OpenAI cost backfill on startup (non-fatal): ", t);
        }
    }
}
