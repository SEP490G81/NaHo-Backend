package org.naho.file.usecase;

import org.naho.file.command.UpdateOperationStatusCommand;
import org.naho.file.command.UploadFileToCloudCommand;
import org.naho.file.exception.FileErrorCode;
import org.naho.file.model.File;
import org.naho.file.model.StoredFile;
import org.naho.file.port.in.CrudFileInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileResultMapperPort;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.file.result.FileResult;
import org.naho.file.type.OperationStatus;
import org.naho.file.type.OperationType;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.exception.ApplicationException;

import java.util.List;

public class CrudFileUseCase implements CrudFileInputPort {

    private final FileRepositoryPort fileRepositoryPort;
    private final FileStorageServicePort fileStorageServicePort;
    private final FileResultMapperPort fileResultMapperPort;

    public CrudFileUseCase(
            FileRepositoryPort fileRepositoryPort,
            FileStorageServicePort fileStorageServicePort,
            FileResultMapperPort fileResultMapperPort
    ) {
        this.fileRepositoryPort = fileRepositoryPort;
        this.fileStorageServicePort = fileStorageServicePort;
        this.fileResultMapperPort = fileResultMapperPort;
    }

    @Override
    public FileResult findById(Long id) {
        if (id == null) {
            throw new ApplicationException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_ID_NULL
            );
        }

        File file = fileRepositoryPort.findById(id);
        return fileResultMapperPort.domainToResult(file);
    }

    @Override
    public List<FileResult> findAllByLeagueIds(List<Long> leagueIds) {
        if (leagueIds == null || leagueIds.isEmpty()) return List.of();
        List<File> files = fileRepositoryPort.findAllByLeagueIds(leagueIds);
        return files
                .stream()
                .map(fileResultMapperPort::domainToResult)
                .toList();
    }

    @Override
    public List<FileResult> findAllByBookIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        List<File> files = fileRepositoryPort.findAllByBookIds(ids);
        return files
                .stream()
                .map(fileResultMapperPort::domainToResult)
                .toList();
    }

    @Override
    public FileResult uploadFileToCloud(UploadFileToCloudCommand command) {
        StoredFile storedFile = command.storedFile();
        if (storedFile == null) {
            throw new ApplicationException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_NOT_VALID
            );
        }

        UpdateOperationStatusCommand updateOperationStatusCommand =
                UpdateOperationStatusCommand
                        .builder()
                        .objectKey(storedFile.objectKey())
                        .operationType(OperationType.UPLOAD)
                        .build();

        File file;
        try {
            fileStorageServicePort.uploadFileToCloud(storedFile, command.isPublic());
            fileStorageServicePort.deleteFileInLocal(storedFile.objectKey());

            updateOperationStatusCommand.setToOperationStatus(OperationStatus.COMPLETED);
            file = fileRepositoryPort.updateOperationByObjectKeyAndOperationType(
                    updateOperationStatusCommand
            );
        } catch (Exception e) {
            updateOperationStatusCommand.setToOperationStatus(OperationStatus.FAILED);
            file = fileRepositoryPort.updateOperationByObjectKeyAndOperationType(
                    updateOperationStatusCommand
            );
        }
        return fileResultMapperPort.domainToResult(file);
    }
}
