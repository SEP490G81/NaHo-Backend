package org.naho.file.usecase;

import org.naho.file.constant.FileProperties;
import org.naho.file.exception.FileErrorCode;
import org.naho.file.mapper.StoredFileMapper;
import org.naho.file.model.File;
import org.naho.file.port.in.UploadFileInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileResultMapperPort;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.file.result.FileResult;
import org.naho.file.result.StoredFile;
import org.naho.file.valueobject.NextRetryAt;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.exception.ApplicationException;

public class UploadFileUseCase implements UploadFileInputPort {

    private final FileRepositoryPort fileRepositoryPort;
    private final FileStorageServicePort fileStorageServicePort;
    private final FileResultMapperPort fileResultMapperPort;
    private final StoredFileMapper storedFileMapper;

    public UploadFileUseCase(
            FileRepositoryPort fileRepositoryPort,
            FileStorageServicePort fileStorageServicePort,
            FileResultMapperPort fileResultMapperPort,
            StoredFileMapper storedFileMapper
    ) {
        this.fileRepositoryPort = fileRepositoryPort;
        this.fileStorageServicePort = fileStorageServicePort;
        this.fileResultMapperPort = fileResultMapperPort;
        this.storedFileMapper = storedFileMapper;
    }

    /**
     * Hàm chỉ được dùng cho upload file lên cloud lần đầu
     *
     * @param storedFile đối tượng chứa các thông tin về file để upload
     * @return FileResult
     */
    @Override
    public FileResult uploadFileToCloud(StoredFile storedFile) {
        if (storedFile == null) {
            throw new ApplicationException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_NOT_VALID
            );
        }

        File file = fileRepositoryPort.findByObjectKey(storedFile.objectKey());

        try {
            // upload file lên cloud
            fileStorageServicePort.uploadFileToCloud(storedFile);

            // nếu thành công thì còn xóa cả file đang lưu tạm trên local
            fileStorageServicePort.deleteFileInLocal(storedFile.objectKey());

            // đánh dấu là đã upload, set các field sau thành:
            // operation status = completed
            // nextRetryAt = null
            file.markCompleted();
        } catch (Exception e) {
            // nếu fail thì đánh dấu là failed và chờ schedule upload lại
            file.markFailed();

            // mặc định retry count = 0
            // nên lần retry tiếp theo sẽ là: sau 1 phút
            file.setNextRetryAt(NextRetryAt.getFromRetryCount(file.getRetryCount()));
        }

        File savedFile = fileRepositoryPort.save(file);

        return fileResultMapperPort.domainToResult(savedFile);
    }

    /**
     * Hàm sử dụng cho schedule để retry những file bị failed
     *
     * @param file File
     */
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
