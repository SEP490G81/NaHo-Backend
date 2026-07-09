package org.naho.config.application.point;

import org.naho.point.mapper.PointHistoryCommandMapper;
import org.naho.point.mapper.PointHistoryResultMapper;
import org.naho.point.port.out.PointHistoryRepositoryPort;
import org.naho.point.port.out.PointSummaryRepositoryPort;
import org.naho.point.usecase.CrudPointHistoryUseCase;
import org.naho.shared.port.out.TransactionPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PointHistoryConfig {
    @Bean
    public PointHistoryCommandMapper pointHistoryCommandMapper() {
        return new PointHistoryCommandMapper();
    }

    @Bean
    public PointHistoryResultMapper pointHistoryResultMapper() {
        return new PointHistoryResultMapper();
    }

    @Bean
    public CrudPointHistoryUseCase crudPointHistoryUseCase(
            PointHistoryCommandMapper pointHistoryCommandMapper,
            PointHistoryResultMapper pointHistoryResultMapper,
            PointHistoryRepositoryPort pointHistoryRepositoryPort,
            TransactionPort transactionPort,
            PointSummaryRepositoryPort pointSummaryRepositoryPort
    ) {
        return new CrudPointHistoryUseCase(
                pointHistoryCommandMapper,
                pointHistoryResultMapper,
                pointHistoryRepositoryPort,
                transactionPort,
                pointSummaryRepositoryPort
        );
    }

}
