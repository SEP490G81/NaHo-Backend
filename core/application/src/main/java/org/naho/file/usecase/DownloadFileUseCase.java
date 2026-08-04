package org.naho.file.usecase;

import org.naho.file.model.File;
import org.naho.file.port.in.DownloadFileInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.file.result.DownloadedFile;
import org.naho.shared.exception.ApplicationException;

public class DownloadFileUseCase implements DownloadFileInputPort {
    private final FileRepositoryPort fileRepositoryPort;
    private final FileStorageServicePort fileStorageServicePort;

    public DownloadFileUseCase(
            FileRepositoryPort fileRepositoryPort,
            FileStorageServicePort fileStorageServicePort
    ) {
        this.fileRepositoryPort = fileRepositoryPort;
        this.fileStorageServicePort = fileStorageServicePort;
    }

    @Override
    public DownloadedFile downloadFileFromCloud(String objectKey) {
        if (objectKey == null) {
            throw new ApplicationException();
        }

        File file = fileRepositoryPort.findByObjectKey(objectKey);
        
        return fileStorageServicePort.downloadFileFromCloud(file);
    }
}
