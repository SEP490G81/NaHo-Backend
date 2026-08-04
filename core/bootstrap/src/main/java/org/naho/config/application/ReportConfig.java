package org.naho.config.application;

import org.naho.file.port.in.CrudFileInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileResultMapperPort;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.social.report.mapper.ReportResultMapper;
import org.naho.social.report.port.in.CreateReportInputPort;
import org.naho.social.report.port.in.GetReportInputPort;
import org.naho.social.report.port.out.ReportRepositoryPort;
import org.naho.social.report.usecase.CreateReportUseCase;
import org.naho.social.report.usecase.GetReportUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReportConfig {

    @Bean
    public ReportResultMapper reportResultMapper(FileResultMapperPort fileResultMapperPort) {
        return new ReportResultMapper(fileResultMapperPort);
    }

    @Bean
    public GetReportInputPort getReportInputPort(ReportRepositoryPort reportRepositoryPort,
                                                  ReportResultMapper reportResultMapper) {
        return new GetReportUseCase(reportRepositoryPort, reportResultMapper);
    }

    @Bean
    public CreateReportInputPort createReportInputPort(
            ReportRepositoryPort reportRepositoryPort,
            FileStorageServicePort fileStorageServicePort,
            FileRepositoryPort fileRepositoryPort,
            CrudFileInputPort crudFileInputPort,
            ReportResultMapper reportResultMapper
    ) {
        return new CreateReportUseCase(
                reportRepositoryPort,
                fileStorageServicePort,
                fileRepositoryPort,
                crudFileInputPort,
                reportResultMapper
        );
    }
}
