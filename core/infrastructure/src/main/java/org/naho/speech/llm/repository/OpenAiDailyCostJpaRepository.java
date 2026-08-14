package org.naho.speech.llm.repository;

import org.naho.speech.llm.entity.OpenAiDailyCostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OpenAiDailyCostJpaRepository extends JpaRepository<OpenAiDailyCostEntity, Long> {

    Optional<OpenAiDailyCostEntity> findByRecordDate(LocalDate recordDate);

    @Query("SELECT SUM(o.costAmount) FROM OpenAiDailyCostEntity o WHERE o.recordDate BETWEEN :startDate AND :endDate")
    Optional<BigDecimal> sumCostBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    List<OpenAiDailyCostEntity> findByRecordDateBetweenOrderByRecordDateAsc(LocalDate fromDate, LocalDate toDate);

    @Query(value = """
                SELECT
                    DATE_FORMAT(record_date, '%Y-%m-01T00:00:00') AS dateOrMonth,
                    SUM(cost_amount) AS cost,
                    COALESCE(MAX(currency), 'USD') AS currency
                FROM openai_daily_costs
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
