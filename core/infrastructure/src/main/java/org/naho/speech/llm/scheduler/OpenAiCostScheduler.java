package org.naho.speech.llm.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.speech.llm.service.OpenAiCostSyncService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiCostScheduler {

    private final OpenAiCostSyncService syncService;

    @Scheduled(cron = "0 1 0 * * ?")
    public void syncDailyOpenAiCosts() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        try {
            syncService.syncDateRange(yesterday, yesterday);
        } catch (Exception e) {
            log.error("[OpenAiCostScheduler] Daily sync failed for date: {}", yesterday, e);
        }
    }
}
