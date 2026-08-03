package org.naho.file.usecase;

import org.naho.file.command.UpdateOperationStatusCommand;
import org.naho.file.exception.FileErrorCode;
import org.naho.file.mapper.FileOperationResultMapper;
import org.naho.file.model.File;
import org.naho.file.model.FileOperation;
import org.naho.file.model.StoredFile;
import org.naho.file.port.in.CrudFileInputPort;
import org.naho.file.port.out.FileOperationRepositoryPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileResultMapperPort;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.file.result.FileOperationResult;
import org.naho.file.result.FileResult;
import org.naho.file.type.OperationStatus;
import org.naho.file.type.OperationType;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;

import java.util.List;

public class CrudFileUseCase implements CrudFileInputPort {

    private final FileRepositoryPort fileRepositoryPort;
    private final FileOperationRepositoryPort fileOperationRepositoryPort;
    private final FileStorageServicePort fileStorageServicePort;
    private final FileResultMapperPort fileResultMapperPort;
    private final FileOperationResultMapper fileOperationResultMapper;
    private final TransactionPort transactionPort;

    public CrudFileUseCase(
            FileRepositoryPort fileRepositoryPort,
            FileOperationRepositoryPort fileOperationRepositoryPort,
            FileStorageServicePort fileStorageServicePort,
            FileResultMapperPort fileResultMapperPort,
            FileOperationResultMapper fileOperationResultMapper,
            TransactionPort transactionPort
    ) {
        this.fileRepositoryPort = fileRepositoryPort;
        this.fileOperationRepositoryPort = fileOperationRepositoryPort;
        this.fileStorageServicePort = fileStorageServicePort;
        this.fileResultMapperPort = fileResultMapperPort;
        this.fileOperationResultMapper = fileOperationResultMapper;
        this.transactionPort = transactionPort;
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
    public FileOperationResult uploadFileToCloud(StoredFile storedFile, boolean isPublic) {
        if (storedFile == null) {
            throw new ApplicationException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_NOT_VALID
            );
        }

        UpdateOperationStatusCommand command = UpdateOperationStatusCommand.builder()
                .objectKey(storedFile.objectKey())
                .operationType(OperationType.UPLOAD)
                .build();

        FileOperation fileOperation;
        // Step 4: Thử upload file lên S3
        try {
            fileStorageServicePort.uploadFileToCloud(storedFile, isPublic);

            // nếu thành công => xóa file đang lưu trong local storage
            fileStorageServicePort.deleteFileInLocal(storedFile.absoluteLocalStoragePath());

            // nếu thành công => cập nhật trạng thái của file operation thành completed
            command.setToOperationStatus(OperationStatus.COMPLETED);
            fileOperation = fileOperationRepositoryPort
                    .updateOperationStatusByObjectKeyAndOperationType(command);
        } catch (Exception e) {
            // nếu thất bại => cập nhật trạng thái của file thành thất bại
            command.setToOperationStatus(OperationStatus.FAILED);
            fileOperation = fileOperationRepositoryPort
                    .updateOperationStatusByObjectKeyAndOperationType(command);
        }
        return fileOperationResultMapper.domainToResult(fileOperation);
    }
}
