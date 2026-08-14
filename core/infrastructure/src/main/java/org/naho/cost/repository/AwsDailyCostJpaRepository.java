package org.naho.cost.repository;

import org.naho.cost.entity.AwsDailyCostEntity;
import org.naho.shared.persistence.BaseJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AwsDailyCostJpaRepository extends BaseJpaRepository<AwsDailyCostEntity> {
    Optional<AwsDailyCostEntity> findByRecordDate(LocalDate recordDate);

    @Query("SELECT MAX(c.recordDate) FROM AwsDailyCostEntity c")
    LocalDate findMaxRecordDate();

    @Query("SELECT SUM(a.costAmount) FROM AwsDailyCostEntity a WHERE a.recordDate BETWEEN :startDate AND :endDate")
    Optional<BigDecimal> sumCostBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    List<AwsDailyCostEntity> findByRecordDateBetweenOrderByRecordDateAsc(LocalDate fromDate, LocalDate toDate);

    @Query(value = """
                SELECT
                    DATE_FORMAT(record_date, '%Y-%m-01T00:00:00') AS dateOrMonth,
                    SUM(cost_amount) AS cost,
                    COALESCE(MAX(currency), 'USD') AS currency
                FROM aws_daily_costs
                WHERE record_date >= :startDate
                GROUP BY DATE_FORMAT(record_date, '%Y-%m-01T00:00:00')
                ORDER BY dateOrMonth ASC
            """, nativeQuery = true)
    List<MonthlyCostProjection> getMonthlyCostsSummary(@Param("startDate") LocalDate startDate);

    interface MonthlyCostProjection {
        String getDateOrMonth();

        BigDecimal getCost();

        String getCurrency();
    }
}
