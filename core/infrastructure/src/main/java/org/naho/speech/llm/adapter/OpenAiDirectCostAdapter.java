package org.naho.speech.llm.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.speech.llm.entity.OpenAiDailyCostEntity;
import org.naho.speech.llm.port.out.OpenAiCostPort;
import org.naho.speech.llm.repository.OpenAiDailyCostJpaRepository;
import org.naho.speech.llm.result.OpenAiCostStatResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiDirectCostAdapter implements OpenAiCostPort {

    private final OpenAiDailyCostJpaRepository repository;

    @Override
    public OpenAiCostStatResult getMonthlyCostStat(YearMonth yearMonth) {
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<OpenAiDailyCostEntity> entities = repository.findByRecordDateBetweenOrderByRecordDateAsc(startDate, endDate);

        Map<LocalDate, BigDecimal> costMap = new HashMap<>();
        BigDecimal totalCostMonthly = BigDecimal.ZERO;

        for (OpenAiDailyCostEntity entity : entities) {
            BigDecimal cost = entity.getCostAmount() != null ? entity.getCostAmount() : BigDecimal.ZERO;
            costMap.put(entity.getRecordDate(), cost);
            totalCostMonthly = totalCostMonthly.add(cost);
        }

        List<OpenAiCostStatResult.DailyCost> dailyCosts = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            BigDecimal dailyCost = costMap.getOrDefault(date, BigDecimal.ZERO);
            dailyCosts.add(new OpenAiCostStatResult.DailyCost(
                    date,
                    dailyCost.setScale(6, RoundingMode.HALF_UP)
            ));
        }

        OpenAiCostStatResult.Summary summary = new OpenAiCostStatResult.Summary(
                totalCostMonthly.setScale(6, RoundingMode.HALF_UP),
                "USD"
        );

        return new OpenAiCostStatResult(
                yearMonth.toString(),
                summary,
                dailyCosts
        );
    }

    @Override
    public OpenAiCostStatResult getAllDailyCosts() {
        List<OpenAiDailyCostEntity> entities = repository.findAllByOrderByRecordDateAsc();

        BigDecimal totalCostAllTime = BigDecimal.ZERO;
        List<OpenAiCostStatResult.DailyCost> dailyCosts = new ArrayList<>();

        for (OpenAiDailyCostEntity entity : entities) {
            BigDecimal cost = entity.getCostAmount() != null ? entity.getCostAmount() : BigDecimal.ZERO;
            totalCostAllTime = totalCostAllTime.add(cost);
            dailyCosts.add(new OpenAiCostStatResult.DailyCost(
                    entity.getRecordDate(),
                    cost.setScale(6, RoundingMode.HALF_UP)
            ));
        }

        OpenAiCostStatResult.Summary summary = new OpenAiCostStatResult.Summary(
                totalCostAllTime.setScale(6, RoundingMode.HALF_UP),
                "USD"
        );

        return new OpenAiCostStatResult(
                "ALL_TIME",
                summary,
                dailyCosts
        );
    }
}
