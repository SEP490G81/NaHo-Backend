package org.naho.speech.llm.usecase;

import org.naho.cost.model.OpenAiDailyCost;
import org.naho.speech.llm.port.in.SyncOpenAiCostInputPort;
import org.naho.speech.llm.port.out.OpenAiCostManagementPort;
import org.naho.speech.llm.port.out.OpenAiCostRepositoryPort;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.logging.Logger;

public class SyncOpenAiCostUseCase implements SyncOpenAiCostInputPort {

    private static final Logger log = Logger.getLogger(SyncOpenAiCostUseCase.class.getName());

    private final OpenAiCostManagementPort openAiCostManagementPort;
    private final OpenAiCostRepositoryPort openAiCostRepositoryPort;

    public SyncOpenAiCostUseCase(OpenAiCostManagementPort openAiCostManagementPort,
                                 OpenAiCostRepositoryPort openAiCostRepositoryPort) {
        this.openAiCostManagementPort = openAiCostManagementPort;
        this.openAiCostRepositoryPort = openAiCostRepositoryPort;
    }

    @Override
    public void syncInitialBackfillIfEmpty() {
        long count = openAiCostRepositoryPort.count();
        if (count == 0) {
            log.info("OpenAI daily costs DB table is empty. Triggering initial backfill for past 6 months...");
            LocalDate startDate = LocalDate.now().minusMonths(6).with(TemporalAdjusters.firstDayOfMonth());
            LocalDate endDate = LocalDate.now();
            syncCustomRange(startDate, endDate);
        } else {
            log.info("OpenAI daily costs DB already contains " + count + " records. Skipping initial backfill.");
        }
    }

    @Override
    public void syncIncremental(int lookbackDays) {
        int effectiveLookback = lookbackDays > 0 ? lookbackDays : 3;
        LocalDate startDate = LocalDate.now().minusDays(effectiveLookback);
        LocalDate endDate = LocalDate.now();
        log.info("Running incremental OpenAI cost sync from " + startDate + " to " + endDate);
        syncCustomRange(startDate, endDate);
    }

    @Override
    public void syncCustomRange(LocalDate fromDate, LocalDate toDate) {
        log.info("Syncing OpenAI daily cost range: " + fromDate + " to " + toDate);
        List<OpenAiDailyCost> fetchedCosts = openAiCostManagementPort.fetchDailyCostsFromOpenAi(fromDate, toDate);
        if (fetchedCosts == null || fetchedCosts.isEmpty()) {
            log.warning("No cost records returned from OpenAI Cost API for range: " + fromDate + " to " + toDate);
            return;
        }
        openAiCostRepositoryPort.saveAll(fetchedCosts);
        log.info("Successfully synced " + fetchedCosts.size() + " OpenAI daily cost records.");
    }
}
