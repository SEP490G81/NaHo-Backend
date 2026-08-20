package org.naho.config.application.point;

import org.naho.point.mapper.PointHistoryCommandMapper;
import org.naho.point.mapper.PointHistoryResultMapper;
import org.naho.point.port.in.CrudPointHistoryInputPort;
import org.naho.point.port.out.PointHistoryRepositoryPort;
import org.naho.point.usecase.CrudPointHistoryUseCase;
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
    public CrudPointHistoryInputPort crudPointHistoryInputPort(
            PointHistoryCommandMapper pointHistoryCommandMapper,
            PointHistoryResultMapper pointHistoryResultMapper,
            PointHistoryRepositoryPort pointHistoryRepositoryPort) {
        return new CrudPointHistoryUseCase(
                pointHistoryCommandMapper,
                pointHistoryResultMapper,
                pointHistoryRepositoryPort);
    }
}
