package org.naho.file.usecase;

import org.naho.file.constant.FileProperties;
import org.naho.file.exception.FileErrorCode;
import org.naho.file.mapper.StoredFileMapper;
import org.naho.file.model.File;
import org.naho.file.port.in.RetryUploadFileInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.file.valueobject.NextRetryAt;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.exception.ApplicationException;

public class RetryUploadFileUseCase implements RetryUploadFileInputPort {

    private final FileRepositoryPort fileRepositoryPort;
    private final FileStorageServicePort fileStorageServicePort;
    private final StoredFileMapper storedFileMapper;

    public RetryUploadFileUseCase(
            FileRepositoryPort fileRepositoryPort,
            FileStorageServicePort fileStorageServicePort,
            StoredFileMapper storedFileMapper
    ) {
        this.fileRepositoryPort = fileRepositoryPort;
        this.fileStorageServicePort = fileStorageServicePort;
        this.storedFileMapper = storedFileMapper;
    }

    @Override
    public void retryUploadFileToCloud(File file) {
        if (file == null) {
            throw new ApplicationException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_NOT_VALID
            );
        }

        // retry count bắt đầu từ 0
        // tăng retry count lên 1
        int retryCount = file.incrementRetryCount();

        int maxRetryCount = FileProperties.MAX_RETRY_COUNT;

        // nếu số lần thử upload lại của file đã vượt giới hạn cho phép
        // cập nhật trạng thái cho file
        if (file.getRetryCount() > maxRetryCount) {
            file.markRetryLimitExceeded();
        } else {
            try {
                // upload file lên cloud
                fileStorageServicePort.uploadFileToCloud(storedFileMapper.domainToStoredFile(file));

                // nếu thành công thì còn xóa cả file đang lưu tạm trên local
                fileStorageServicePort.deleteFileInLocal(file.getObjectKey());

                // đánh dấu là đã upload, set các field sau thành:
                // operation status = completed
                // nextRetryAt = null
                file.markCompleted();
            } catch (Exception e) {
                // cập nhật lại thời điểm retry tiếp theo
                file.setNextRetryAt(NextRetryAt.getFromRetryCount(retryCount));
            }
        }
        fileRepositoryPort.save(file);
    }

}
