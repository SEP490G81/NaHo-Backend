package org.naho.file.mapper;

import org.naho.file.model.File;
import org.naho.file.result.FileResult;

public class FileResultMapper {
    public FileResult domainToResult(File domain) {
        return new FileResult(
                domain.getId(),
                domain.getFileUrl(),
                domain.getPreviewUrl(),
                domain.getOriginalName(),
                domain.getContentType(),
                domain.getSize()
        );
    }
}
