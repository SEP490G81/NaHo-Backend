package org.naho.file.port.out;

import org.naho.file.model.File;
import org.naho.file.result.StoredFile;
import org.naho.file.type.OperationStatus;
import org.naho.file.type.OperationType;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface FileRepositoryPort {
    Optional<File> findById(Long id);

    List<File> findAllByLeagueIds(List<Long> leagueIds);

    List<File> findAllByBookIds(List<Long> ids);

    File findByObjectKey(String objectKey);

    File createNewForUpload(StoredFile storedFile, boolean isPublic);

    File save(File file);

    List<File> findAllForSchedulerRetry(
            Instant now,
            OperationType operationType,
            OperationStatus operationStatus
    );

    List<File> findAllForSchedulerRetryDelete(
            Instant now,
            OperationType operationType
    );

    void deleteById(Long id);

    List<File> findAllByReportId(Long reportId);

    Optional<File> findAvatarFileByUserId(Long userId);
}
