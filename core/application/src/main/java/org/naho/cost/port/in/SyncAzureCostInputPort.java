package org.naho.cost.port.in;

import java.time.LocalDate;

public interface SyncAzureCostInputPort {
    void syncFullBackfill(LocalDate startDate);

    void syncIncremental(int lookbackDays);

    void syncCustomRange(LocalDate fromDate, LocalDate toDate);
}
