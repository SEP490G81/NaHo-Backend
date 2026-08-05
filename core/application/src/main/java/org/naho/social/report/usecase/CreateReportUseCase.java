package org.naho.social.report.usecase;

import org.naho.file.model.File;
import org.naho.file.model.StoredFile;
import org.naho.file.port.in.CrudFileInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.social.report.command.CreateReportCommand;
import org.naho.social.report.mapper.ReportResultMapper;
import org.naho.social.report.model.Report;
import org.naho.social.report.port.in.CreateReportInputPort;
import org.naho.social.report.port.out.ReportRepositoryPort;
import org.naho.social.report.result.ReportResult;

import java.util.List;

public class CreateReportUseCase implements CreateReportInputPort {

    private final ReportRepositoryPort reportRepositoryPort;
    private final FileStorageServicePort fileStorageServicePort;
    private final FileRepositoryPort fileRepositoryPort;
    private final CrudFileInputPort crudFileInputPort;
    private final ReportResultMapper reportResultMapper;

    public CreateReportUseCase(
            ReportRepositoryPort reportRepositoryPort,
            FileStorageServicePort fileStorageServicePort,
            FileRepositoryPort fileRepositoryPort,
            CrudFileInputPort crudFileInputPort,
            ReportResultMapper reportResultMapper
    ) {
        this.reportRepositoryPort = reportRepositoryPort;
        this.fileStorageServicePort = fileStorageServicePort;
        this.fileRepositoryPort = fileRepositoryPort;
        this.crudFileInputPort = crudFileInputPort;
        this.reportResultMapper = reportResultMapper;
    }

    @Override
    public ReportResult createReport(CreateReportCommand command) {
        Report report = reportResultMapper.commandToDomain(command);

        Report savedReport = reportRepositoryPort.save(report);

        if (command.imageFiles() != null && !command.imageFiles().isEmpty()) {
            for (Object fileObj : command.imageFiles()) {
                if (fileObj == null) {
                    continue;
                }
                StoredFile storedFile = fileStorageServicePort.saveReportFileToLocal(fileObj);
                fileRepositoryPort.saveReportFileToDbForUpload(storedFile, savedReport.getId(), true);
                crudFileInputPort.uploadFileToCloud(storedFile, true);
            }
        }

        List<File> reportFiles = fileRepositoryPort.findAllByReportId(savedReport.getId());

        return reportResultMapper.domainToResult(savedReport, reportFiles);
    }
}
