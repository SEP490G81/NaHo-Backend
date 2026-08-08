package org.naho.social.report.usecase;

import org.naho.file.constant.FileAccessStatus;
import org.naho.file.model.File;
import org.naho.file.port.in.UploadFileInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.file.result.FileResult;
import org.naho.file.result.StoredFile;
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

    public CreateReportUseCase(
            ReportRepositoryPort reportRepositoryPort,
            FileStorageServicePort fileStorageServicePort,
            FileRepositoryPort fileRepositoryPort,
            ReportResultMapper reportResultMapper,
            UploadFileInputPort uploadFileInputPort,
            TransactionPort transactionPort
    ) {
        this.reportRepositoryPort = reportRepositoryPort;
        this.fileStorageServicePort = fileStorageServicePort;
        this.fileRepositoryPort = fileRepositoryPort;
        this.reportResultMapper = reportResultMapper;
        this.uploadFileInputPort = uploadFileInputPort;
        this.transactionPort = transactionPort;
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

            File file = fileRepositoryPort.createNewForUpload(storedFile, FileAccessStatus.PRIVATE);

            files.add(file);
        }

        savedReport.setFiles(files);

        return reportResultMapper.domainToResult(savedReport);
    }
}
