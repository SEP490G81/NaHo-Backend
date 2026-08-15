package org.naho.cost.port.in;

import java.time.LocalDate;

public interface SyncOpenAiCostInputPort {
    void syncInitialBackfillIfEmpty();

    void syncIncremental(int lookbackDays);

    void syncCustomRange(LocalDate fromDate, LocalDate toDate);
}
