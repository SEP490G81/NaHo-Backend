package org.naho.file.usecase;

import org.naho.file.exception.FileErrorCode;
import org.naho.file.model.File;
import org.naho.file.port.in.DeleteFileInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileStorageServicePort;
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

        try {
            fileStorageServicePort.deleteFileInCloud(file);

            fileRepositoryPort.deleteById(file.getId());
            
        } catch (Exception e) {

        }
    }
}
