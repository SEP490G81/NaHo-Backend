package org.naho.book.usecase;

import org.naho.book.port.in.UploadBookCoverImageInputPort;
import org.naho.file.constant.FileAccessStatus;
import org.naho.file.port.in.UploadFileInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.result.FileResult;
import org.naho.file.result.StoredFile;
import org.naho.shared.port.out.TransactionPort;

public class UploadBookCoverImageUseCase implements UploadBookCoverImageInputPort {

    private final FileRepositoryPort fileRepositoryPort;
    private final UploadFileInputPort uploadFileInputPort;
    private final TransactionPort transactionPort;

    public UploadBookCoverImageUseCase(
            FileRepositoryPort fileRepositoryPort,
            UploadFileInputPort uploadFileInputPort,
            TransactionPort transactionPort
    ) {
        this.fileRepositoryPort = fileRepositoryPort;
        this.uploadFileInputPort = uploadFileInputPort;
        this.transactionPort = transactionPort;
    }

    @Override
    public FileResult uploadCoverImage(StoredFile storedFile) {
        // 1. Lưu metadata của file vào DB với trạng thái PUBLIC
        transactionPort.execute(() -> {
            fileRepositoryPort.createNewForUpload(storedFile, FileAccessStatus.PUBLIC);
            return null;
        });

        // 2. Thực hiện upload lên Cloud và cập nhật trạng thái trong DB
        return uploadFileInputPort.uploadFileToCloud(storedFile);
    }
}
