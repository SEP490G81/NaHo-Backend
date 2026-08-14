package org.naho.speech.azure.port.in;

import java.time.LocalDate;

public interface SyncAzureCostInputPort {
    void syncInitialBackfillIfEmpty();
    void syncIncremental(int lookbackDays);
    void syncCustomRange(LocalDate fromDate, LocalDate toDate);
}
