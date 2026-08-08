package org.naho.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.file.model.File;
import org.naho.file.port.in.AsyncCrudFileInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.type.OperationStatus;
import org.naho.file.type.OperationType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileUploadRetryScheduler {
    private final FileRepositoryPort fileRepositoryPort;
    private final AsyncCrudFileInputPort asyncCrudFileInputPort;

    /**
     * Khi hàm này kết thúc thì phải đợi thêm thời gian delay thì mới chạy tiếp.
     * Ví dụ 10:00 chạy, upload mất 1 phút, thì 10:03 mới chạy tiếp.
     */
    @Scheduled(fixedDelayString = "${app.scheduler.file-upload-retry.fixed-delay}")
    public void retryUploadFiles() {
        log.info("Retry upload files!");

        Instant now = Instant.now();

        List<File> files = fileRepositoryPort.findAllForSchedulerRetryUpload(
                now,
                OperationType.UPLOAD,
                OperationStatus.FAILED
        );

        if (files.isEmpty()) {
            return;
        }

        for (File file : files) {
            asyncCrudFileInputPort.retryUploadFileToCloudAsync(file);
        }

        log.info("Retry upload {} files completed!", files.size());
    }
}
