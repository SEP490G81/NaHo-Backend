package org.naho.file.usecase;

import org.naho.file.exception.FileErrorCode;
import org.naho.file.model.File;
import org.naho.file.port.in.DeleteFileInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.file.type.OperationType;
import org.naho.file.valueobject.NextRetryAt;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.exception.ApplicationException;

public class DeleteFileUseCase implements DeleteFileInputPort {
    private final FileRepositoryPort fileRepositoryPort;
    private final FileStorageServicePort fileStorageServicePort;

    public DeleteFileUseCase(
            FileRepositoryPort fileRepositoryPort,
            FileStorageServicePort fileStorageServicePort
    ) {
        this.fileRepositoryPort = fileRepositoryPort;
        this.fileStorageServicePort = fileStorageServicePort;
    }

    @Override
    public void deleteFileInCloud(String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            throw new ApplicationException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_OBJECT_KEY_EMPTY
            );
        }

        File file = fileRepositoryPort.findByObjectKey(objectKey);

        if (!OperationType.DELETE.equals(file.getOperationType())) {
            throw new ApplicationException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_NOT_VALID
            );
        }

        try {
            // thử xóa trên cloud
            fileStorageServicePort.deleteFileInCloud(file);

            // nếu thành công thì xóa hẳn trong database
            fileRepositoryPort.deleteById(file.getId());

        } catch (Exception e) {
            // nếu thất bại thì đánh dấu trạng thái thành FAILED
            file.markFailed();

            // và cập nhật lần được retry tiếp theo
            // retry count lúc này là 0
            file.setNextRetryAt(NextRetryAt.getFromRetryCount(file.getRetryCount()));

            // lưu các trạng thái mới của file
            fileRepositoryPort.save(file);
        }
    }
}
