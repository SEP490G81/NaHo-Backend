package org.naho.config.application;

import org.naho.social.report.mapper.ReportResultMapper;
import org.naho.social.report.port.in.GetReportInputPort;
import org.naho.social.report.port.out.ReportRepositoryPort;
import org.naho.social.report.usecase.GetReportUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReportConfig {

    @Bean
    public ReportResultMapper reportResultMapper() {
        return new ReportResultMapper();
    }

    @Bean
    public GetReportInputPort getReportInputPort(ReportRepositoryPort reportRepositoryPort,
                                                 ReportResultMapper reportResultMapper) {
        return new GetReportUseCase(reportRepositoryPort, reportResultMapper);
    }

}
