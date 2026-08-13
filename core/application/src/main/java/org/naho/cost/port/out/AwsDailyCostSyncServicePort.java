package org.naho.cost.port.out;

import java.time.LocalDate;

public interface AwsDailyCostSyncServicePort {
    void sync(LocalDate from, LocalDate to);
}
