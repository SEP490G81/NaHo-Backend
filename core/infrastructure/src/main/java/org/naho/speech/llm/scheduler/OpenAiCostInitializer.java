package org.naho.speech.llm.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.speech.llm.service.OpenAiCostSyncService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiCostInitializer implements ApplicationRunner {

    private final OpenAiCostSyncService syncService;

    @Override
    public void run(ApplicationArguments args) {
        // Sync historical data from 6 months ago up to today
        LocalDate startDate = LocalDate.now().minusMonths(6);
        LocalDate today = LocalDate.now();

        try {
            syncService.syncDateRange(startDate, today);
        } catch (Exception e) {
            log.error("[OpenAiCostInitializer] Startup OpenAI cost sync failed for date range {} to {}", startDate, today, e);
        }
    }
}
