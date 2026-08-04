package org.naho.file.usecase;

import org.naho.file.exception.FileErrorCode;
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

    public UploadFileUseCase(
            FileRepositoryPort fileRepositoryPort,
            FileStorageServicePort fileStorageServicePort,
            FileResultMapperPort fileResultMapperPort
    ) {
        this.fileRepositoryPort = fileRepositoryPort;
        this.fileStorageServicePort = fileStorageServicePort;
        this.fileResultMapperPort = fileResultMapperPort;
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
}
