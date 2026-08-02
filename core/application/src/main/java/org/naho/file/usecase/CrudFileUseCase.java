package org.naho.file.usecase;

import org.naho.file.command.UpdateOperationStatusCommand;
import org.naho.file.exception.FileErrorCode;
import org.naho.file.model.File;
import org.naho.file.model.FileOperation;
import org.naho.file.model.StoredFile;
import org.naho.file.port.in.CrudFileInputPort;
import org.naho.file.port.out.FileOperationRepositoryPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileResultMapperPort;
import org.naho.file.port.out.FileStorageServicePort;
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
    private final TransactionPort transactionPort;

    public CrudFileUseCase(
            FileRepositoryPort fileRepositoryPort,
            FileOperationRepositoryPort fileOperationRepositoryPort,
            FileStorageServicePort fileStorageServicePort,
            FileResultMapperPort fileResultMapperPort,
            TransactionPort transactionPort
    ) {
        this.fileRepositoryPort = fileRepositoryPort;
        this.fileOperationRepositoryPort = fileOperationRepositoryPort;
        this.fileStorageServicePort = fileStorageServicePort;
        this.fileResultMapperPort = fileResultMapperPort;
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
    public void uploadFileToCloud(StoredFile storedFile) {
        if (storedFile == null) {
            throw new ApplicationException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_NOT_VALID
            );
        }

        // Step 4: Thử upload file lên S3
        try {
            fileStorageServicePort.uploadFileToCloud(storedFile);

            // nếu thành công => xóa file đang lưu trong local storage
            fileStorageServicePort.deleteFileInLocal(storedFile.absoluteLocalStoragePath());

            // nếu thành công => cập nhật trạng thái của file operation thành completed
            fileOperationRepositoryPort.updateOperationStatusByObjectKeyAndOperationType(
                    UpdateOperationStatusCommand.builder()
                            .objectKey(storedFile.objectKey())
                            .operationType(OperationType.UPLOAD)
                            .toOperationStatus(OperationStatus.COMPLETED)
                            .build()
            );
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public FileResult saveFileToDbForUpload(StoredFile storedFile) {
        return transactionPort.execute(() -> doSaveFileToDbForUpload(storedFile));
    }

    private FileResult doSaveFileToDbForUpload(StoredFile storedFile) {
        if (storedFile == null) {
            throw new ApplicationException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_NOT_VALID
            );
        }

        // Step 2: Lưu file vào db (FileEntity)
        File domain = File.builder()
                .objectKey(storedFile.objectKey())
                .originalFileName(storedFile.originalFileName())
                .contentType(storedFile.contentType())
                .size(storedFile.size())
                .build();

        File savedFile = fileRepositoryPort.createNew(domain);

        // Step 3: Lưu file operation vào db (FileOperationEntity)
        FileOperation fileOperation = FileOperation.builder()
                .fileId(savedFile.getId())
                .operationType(OperationType.UPLOAD)
                .operationStatus(OperationStatus.PENDING)
                .retryCount(0)
                .build();

        fileOperationRepositoryPort.save(fileOperation);
        return fileResultMapperPort.domainToResult(savedFile);
    }
}
