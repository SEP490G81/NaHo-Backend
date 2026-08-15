package org.naho.cost.port.out;

import org.naho.cost.model.OpenAiDailyCost;

import java.time.LocalDate;
import java.util.List;

public interface OpenAiCostManagementPort {
    List<OpenAiDailyCost> fetchDailyCostsFromOpenAi(LocalDate fromDate, LocalDate toDate);
}
