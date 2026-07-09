package org.naho.config.application;

import org.naho.point.mapper.PointSummaryResultMapper;
import org.naho.point.port.out.PointSummaryRepositoryPort;
import org.naho.point.usecase.CrudPointSummaryUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PointSummaryConfig {
    
    @Bean
    public PointSummaryResultMapper pointSummaryResultMapper() {
        return new PointSummaryResultMapper();
    }

    @Bean
    public CrudPointSummaryUseCase crudPointSummaryUseCase(
            PointSummaryRepositoryPort pointSummaryRepositoryPort,
            PointSummaryResultMapper pointSummaryResultMapper
    ) {
        return new CrudPointSummaryUseCase(
                pointSummaryRepositoryPort,
                pointSummaryResultMapper
        );
    }
}
