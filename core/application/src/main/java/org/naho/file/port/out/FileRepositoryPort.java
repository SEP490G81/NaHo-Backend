package org.naho.file.port.out;

import org.naho.file.command.UpdateOperationStatusCommand;
import org.naho.file.model.File;
import org.naho.file.result.StoredFile;
import org.naho.file.type.OperationStatus;
import org.naho.file.type.OperationType;

import java.time.Instant;
import java.util.List;

public interface FileRepositoryPort {
    File findById(Long id);

    List<File> findAllByLeagueIds(List<Long> leagueIds);

    List<File> findAllByBookIds(List<Long> ids);

    File findByObjectKey(String objectKey);

    File createNewForUpload(StoredFile storedFile, boolean isPublic);

    File updateOperationByObjectKeyAndOperationType(UpdateOperationStatusCommand command);

    File updateOperationStatus(File file, OperationStatus operationStatus);

    File save(File file);

    List<File> findAllForSchedulerRetryUpload(
            Instant now,
            OperationType operationType,
            OperationStatus operationStatus
    );
}
