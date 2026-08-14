package org.naho.speech.azure.port.out;

import org.naho.speech.azure.model.AzureDailyCost;

import java.time.LocalDate;
import java.util.List;

public interface AzureCostManagementPort {
    List<AzureDailyCost> fetchDailyCostsFromAzure(LocalDate fromDate, LocalDate toDate);
}
