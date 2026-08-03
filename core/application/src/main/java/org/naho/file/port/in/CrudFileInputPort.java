package org.naho.file.port.in;

import org.naho.file.model.StoredFile;
import org.naho.file.result.FileOperationResult;
import org.naho.file.result.FileResult;

import java.util.List;

public interface CrudFileInputPort {
    FileResult findById(Long id);

    List<FileResult> findAllByLeagueIds(List<Long> leagueIds);

    List<FileResult> findAllByBookIds(List<Long> ids);

    FileOperationResult uploadFileToCloud(StoredFile storedFile, boolean isPublic);
}
