package org.naho.file.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.file.model.File;
import org.naho.file.port.in.AsyncUploadFileInputPort;
import org.naho.file.port.in.UploadFileInputPort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AsyncUploadFileAdapter implements AsyncUploadFileInputPort {
    private final UploadFileInputPort uploadFileInputPort;

    @Async("uploadFileToCloudExecutor")
    @Override
    public void retryUploadFileToCloudAsync(File file) {
        uploadFileInputPort.retryUploadFileToCloud(file);
    }
}
