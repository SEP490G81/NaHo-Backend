package org.naho.speech.azure.usecase;

import org.naho.speech.azure.model.AzureDailyCost;
import org.naho.speech.azure.port.in.SyncAzureCostInputPort;
import org.naho.speech.azure.port.out.AzureCostManagementPort;
import org.naho.speech.azure.port.out.AzureCostRepositoryPort;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.logging.Logger;

public class SyncAzureCostUseCase implements SyncAzureCostInputPort {

    private static final Logger log = Logger.getLogger(SyncAzureCostUseCase.class.getName());

    private final AzureCostManagementPort azureCostManagementPort;
    private final AzureCostRepositoryPort azureCostRepositoryPort;

    public SyncAzureCostUseCase(AzureCostManagementPort azureCostManagementPort,
                               AzureCostRepositoryPort azureCostRepositoryPort) {
        this.azureCostManagementPort = azureCostManagementPort;
        this.azureCostRepositoryPort = azureCostRepositoryPort;
    }

    @Override
    public void syncInitialBackfillIfEmpty() {
        long count = azureCostRepositoryPort.count();
        if (count == 0) {
            log.info("Azure daily costs DB table is empty. Triggering initial backfill for past 6 months...");
            LocalDate startDate = LocalDate.now().minusMonths(6).with(TemporalAdjusters.firstDayOfMonth());
            LocalDate endDate = LocalDate.now();
            syncCustomRange(startDate, endDate);
        } else {
            log.info("Azure daily costs DB already contains " + count + " records. Skipping initial backfill.");
        }
    }

    @Override
    public void syncIncremental(int lookbackDays) {
        int effectiveLookback = lookbackDays > 0 ? lookbackDays : 3;
        LocalDate startDate = LocalDate.now().minusDays(effectiveLookback);
        LocalDate endDate = LocalDate.now();
        log.info("Running incremental Azure cost sync from " + startDate + " to " + endDate);
        syncCustomRange(startDate, endDate);
    }

    @Override
    public void syncCustomRange(LocalDate fromDate, LocalDate toDate) {
        log.info("Syncing Azure daily cost range: " + fromDate + " to " + toDate);
        List<AzureDailyCost> fetchedCosts = azureCostManagementPort.fetchDailyCostsFromAzure(fromDate, toDate);
        if (fetchedCosts == null || fetchedCosts.isEmpty()) {
            log.warning("No cost records returned from Azure Cost API for range: " + fromDate + " to " + toDate);
            return;
        }
        azureCostRepositoryPort.saveAll(fetchedCosts);
        log.info("Successfully synced " + fetchedCosts.size() + " Azure daily cost records.");
    }
}
