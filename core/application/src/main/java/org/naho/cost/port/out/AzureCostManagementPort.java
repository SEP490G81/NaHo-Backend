package org.naho.cost.port.out;

import org.naho.cost.model.AzureDailyCost;

import java.time.LocalDate;
import java.util.List;

public interface AzureCostManagementPort {
    List<AzureDailyCost> fetchDailyCostsFromAzure(LocalDate fromDate, LocalDate toDate);
}
