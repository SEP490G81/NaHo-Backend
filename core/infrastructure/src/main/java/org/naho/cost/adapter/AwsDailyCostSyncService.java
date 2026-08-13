package org.naho.cost.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.cost.entity.AwsDailyCostEntity;
import org.naho.cost.helper.AwsCostExtractor;
import org.naho.cost.port.out.AwsDailyCostSyncServicePort;
import org.naho.cost.repository.AwsDailyCostJpaRepository;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.costexplorer.model.GetCostAndUsageResponse;
import software.amazon.awssdk.services.costexplorer.model.ResultByTime;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AwsDailyCostSyncService implements AwsDailyCostSyncServicePort {
    private final AwsCostExplorerClient awsCostExplorerClient;
    private final AwsDailyCostJpaRepository awsDailyCostJpaRepository;
    private final AwsCostExtractor awsCostExtractor;

    /**
     * Method dùng để đồng bộ chi phí AWS trong 1 khoảng ngày [from, to] vào db
     *
     * @param from từ ngày
     * @param to   đến ngày
     */
    @Override
    public void sync(LocalDate from, LocalDate to) {
        GetCostAndUsageResponse response = awsCostExplorerClient.getDailyCost(from, to);

        List<AwsDailyCostEntity> entities = new ArrayList<>();

        // lặp và add
        for (ResultByTime resultByTime : response.resultsByTime()) {
            LocalDate recordDate = LocalDate.parse(resultByTime.timePeriod().start());

            BigDecimal costAmount = awsCostExtractor.extractCost(resultByTime);

            String currency = awsCostExtractor.extractCurrency(resultByTime);

            AwsDailyCostEntity currentEntity = awsDailyCostJpaRepository
                    .findByRecordDate(recordDate)
                    .orElse(new AwsDailyCostEntity());

            currentEntity.setRecordDate(recordDate);
            currentEntity.setCostAmount(costAmount);
            currentEntity.setCurrency(currency);

            entities.add(currentEntity);
        }

        // save all
        awsDailyCostJpaRepository.saveAll(entities);
    }
}
