package org.naho.config.application;

import org.naho.social.report.mapper.ReportResultMapper;
import org.naho.social.report.port.in.GetListReportByAdminInputPort;
import org.naho.social.report.port.in.GetListReportByContentManagerInputPort;
import org.naho.social.report.port.in.GetReportInputPort;
import org.naho.social.report.port.out.ReportRepositoryPort;
import org.naho.social.report.usecase.GetDetailReportUseCase;
import org.naho.social.report.usecase.GetListReportByAdminUsecase;
import org.naho.social.report.usecase.GetListReportByContentManagerUsecase;
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
        return new GetDetailReportUseCase(reportRepositoryPort, reportResultMapper);
    }

    @Bean
    public GetListReportByAdminInputPort getListReportByAdminInputPort(ReportRepositoryPort reportRepositoryPort,
                                                                       ReportResultMapper reportResultMapper) {
        return new GetListReportByAdminUsecase(reportRepositoryPort, reportResultMapper);
    }

    @Bean
    public GetListReportByContentManagerInputPort getListReportByContentManagerInputPort(ReportRepositoryPort reportRepositoryPort,
                                                                                         ReportResultMapper reportResultMapper) {
        return new GetListReportByContentManagerUsecase(reportRepositoryPort, reportResultMapper);
    }
}
