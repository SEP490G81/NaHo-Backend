package org.naho.file.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.file.model.File;
import org.naho.file.port.in.AsyncUploadFileInputPort;
import org.naho.file.port.in.RetryUploadFileInputPort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AsyncUploadFileAdapter implements AsyncUploadFileInputPort {
    private final RetryUploadFileInputPort retryUploadFileInputPort;

    @Async("uploadFileToCloudExecutor")
    @Override
    public void retryUploadFileToCloudAsync(File file) {
        retryUploadFileInputPort.retryUploadFileToCloud(file);
    }
}
