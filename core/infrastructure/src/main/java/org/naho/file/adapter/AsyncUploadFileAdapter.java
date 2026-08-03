package org.naho.file.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.file.command.UploadFileToCloudCommand;
import org.naho.file.model.StoredFile;
import org.naho.file.port.in.AsyncUploadFileInputPort;
import org.naho.file.port.in.CrudFileInputPort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AsyncUploadFileAdapter implements AsyncUploadFileInputPort {
    private final CrudFileInputPort crudFileInputPort;

    @Async("uploadFileToCloudExecutor")
    @Override
    public void uploadFileToCloud(StoredFile file, boolean isPublic) {
        crudFileInputPort.uploadFileToCloud(
                UploadFileToCloudCommand.builder()
                        .storedFile(file)
                        .isPublic(isPublic)
                        .isRetry(false)
                        .build()
        );
    }
}
