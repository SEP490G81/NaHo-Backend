package org.naho.social.report.usecase;

import org.naho.file.model.File;
import org.naho.file.port.in.UploadFileInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.file.result.FileResult;
import org.naho.file.result.StoredFile;
import org.naho.notification.event.SendNotificationEvent;
import org.naho.notification.type.NotificationType;
import org.naho.shared.port.out.EventPublisherPort;
import org.naho.shared.port.out.TransactionPort;
import org.naho.social.report.command.CreateReportCommand;
import org.naho.social.report.mapper.ReportResultMapper;
import org.naho.social.report.model.Report;
import org.naho.social.report.port.in.CreateReportInputPort;
import org.naho.social.report.port.out.ReportRepositoryPort;
import org.naho.social.report.result.ReportResult;

import java.util.ArrayList;
import java.util.List;

public class CreateReportUseCase implements CreateReportInputPort {

    private final ReportRepositoryPort reportRepositoryPort;
    private final FileStorageServicePort fileStorageServicePort;
    private final FileRepositoryPort fileRepositoryPort;
    private final ReportResultMapper reportResultMapper;
    private final UploadFileInputPort uploadFileInputPort;
    private final TransactionPort transactionPort;
    private final EventPublisherPort eventPublisherPort;

    public CreateReportUseCase(
            ReportRepositoryPort reportRepositoryPort,
            FileStorageServicePort fileStorageServicePort,
            FileRepositoryPort fileRepositoryPort,
            ReportResultMapper reportResultMapper,
            UploadFileInputPort uploadFileInputPort,
            TransactionPort transactionPort,
            EventPublisherPort eventPublisherPort
    ) {
        this.reportRepositoryPort = reportRepositoryPort;
        this.fileStorageServicePort = fileStorageServicePort;
        this.fileRepositoryPort = fileRepositoryPort;
        this.reportResultMapper = reportResultMapper;
        this.uploadFileInputPort = uploadFileInputPort;
        this.transactionPort = transactionPort;
        this.eventPublisherPort = eventPublisherPort;
    }

    @Override
    public ReportResult createReport(CreateReportCommand command) {
        ReportResult result = transactionPort.execute(() -> doUploadFile(command));

        List<FileResult> fileResults = new ArrayList<>();

        for (StoredFile storedFile : command.imageFiles()) {
            if (storedFile != null) {
                FileResult uploadedFile = uploadFileInputPort.uploadFileToCloud(storedFile);
                fileResults.add(uploadedFile);
            }
        }

        result.setFiles(fileResults);

        return result;
    }

    private ReportResult doUploadFile(CreateReportCommand command) {
        Report report = reportResultMapper.commandToDomain(command);

        Report savedReport = reportRepositoryPort.save(report);

        List<StoredFile> imageFiles = command.imageFiles();

        List<File> files = new ArrayList<>();
        for (StoredFile storedFile : imageFiles) {
            storedFile.setReportId(savedReport.getId());

            File file = fileRepositoryPort.createNewForUpload(storedFile, false);

            files.add(file);
        }

        savedReport.setFiles(files);

        // Notify Admin
        // Typically Admin user ID would be fixed, or fetched from DB.
        // For demonstration (or specific admin assignment), we might hardcode or use a configuration.
        // Assuming user ID 1 is admin.
        String metadata = "{\"reportId\": " + savedReport.getId() + ", \"reportType\": \"" + savedReport.getReportType() + "\"}";
        eventPublisherPort.publish(new SendNotificationEvent(
                this,
                1L, // ADMIN_ID
                NotificationType.REPORT,
                "Có báo cáo mới",
                "Người dùng vừa gửi một báo cáo mới",
                null,
                metadata
        ));

        return reportResultMapper.domainToResult(savedReport);


    }
}

