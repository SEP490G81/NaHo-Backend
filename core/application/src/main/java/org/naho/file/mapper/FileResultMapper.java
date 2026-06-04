package org.naho.file.mapper;

import org.naho.file.model.File;
import org.naho.file.result.FileResult;

public class FileResultMapper {
    public FileResult domainToResult(File domain) {
        if (domain == null) return null;
        return new FileResult(
                domain.getId(),
                domain.getObjectKey(),
                domain.getPreviewKey(),
                domain.getOriginalName(),
                domain.getContentType(),
                domain.getSize()
        );
    }
}
