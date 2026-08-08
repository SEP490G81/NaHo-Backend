package org.naho.file.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.file.model.File;
import org.naho.file.port.in.AsyncCrudFileInputPort;
import org.naho.file.port.in.DeleteFileInputPort;
import org.naho.file.port.in.UploadFileInputPort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AsyncCrudFileAdapter implements AsyncCrudFileInputPort {
    private final UploadFileInputPort uploadFileInputPort;
    private final DeleteFileInputPort deleteFileInputPort;

    @Async("retryUploadFileToCloudAsync")
    @Override
    public void retryUploadFileToCloudAsync(File file) {
        uploadFileInputPort.retryUploadFileToCloud(file);
    }

    @Async("deleteFileInCloudExecutor")
    @Override
    public void deleteFileInCloudAsync(String objectKey) {
        deleteFileInputPort.deleteFileInCloud(objectKey);
    }
}
