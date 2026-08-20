package org.naho.persona.usecase;

import org.naho.file.constant.FileAccessStatus;
import org.naho.file.port.in.UploadFileInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.result.FileResult;
import org.naho.file.result.StoredFile;
import org.naho.persona.port.in.UploadPersonaAvatarInputPort;
import org.naho.shared.port.out.TransactionPort;

public class UploadPersonaAvatarUseCase implements UploadPersonaAvatarInputPort {

    private final FileRepositoryPort fileRepositoryPort;
    private final UploadFileInputPort uploadFileInputPort;
    private final TransactionPort transactionPort;

    public UploadPersonaAvatarUseCase(
            FileRepositoryPort fileRepositoryPort,
            UploadFileInputPort uploadFileInputPort,
            TransactionPort transactionPort
    ) {
        this.fileRepositoryPort = fileRepositoryPort;
        this.uploadFileInputPort = uploadFileInputPort;
        this.transactionPort = transactionPort;
    }

    @Override
    public FileResult uploadAvatar(StoredFile storedFile) {
        // 1. Lưu metadata của file vào DB với trạng thái PUBLIC
        transactionPort.execute(() -> {
            fileRepositoryPort.createNewForUpload(storedFile, FileAccessStatus.PUBLIC);
            return null;
        });

        // 2. Thực hiện upload lên Cloud và cập nhật trạng thái trong DB
        return uploadFileInputPort.uploadFileToCloud(storedFile);
    }
}
