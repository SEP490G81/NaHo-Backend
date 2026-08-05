package org.naho.config.application;

import org.naho.file.port.in.CrudFileInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileResultMapperPort;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.shared.port.out.EmailPort;
import org.naho.social.report.mapper.ReportResultMapper;
import org.naho.social.report.port.in.CreateReportInputPort;
import org.naho.social.report.port.in.GetListReportByUserInputPort;
import org.naho.social.report.port.in.GetReportInputPort;
import org.naho.social.report.port.in.UpdateReportStatusInputPort;
import org.naho.social.report.port.out.ReportRepositoryPort;
import org.naho.social.report.usecase.CreateReportUseCase;
import org.naho.social.report.usecase.GetListReportByUserUseCase;
import org.naho.social.report.usecase.GetReportUseCase;
import org.naho.social.report.usecase.UpdateReportStatusUseCase;
import org.naho.user.port.out.UserRepositoryPort;
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
    public GetListReportByUserInputPort getListReportByUserInputPort(ReportRepositoryPort reportRepositoryPort,
                                                                     ReportResultMapper reportResultMapper) {
        return new GetListReportByUserUseCase(reportRepositoryPort, reportResultMapper);
    }

    @Bean
    public UpdateReportStatusInputPort updateReportStatusInputPort(
            ReportRepositoryPort reportRepositoryPort,
            ReportResultMapper reportResultMapper,
            UserRepositoryPort userRepositoryPort,
            EmailPort emailPort
    ) {
        return new UpdateReportStatusUseCase(reportRepositoryPort, reportResultMapper, userRepositoryPort, emailPort);
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
