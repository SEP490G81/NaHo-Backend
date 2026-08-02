package org.naho.file.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.file.dto.response.FileResponse;
import org.naho.file.result.FileResult;

@Mapper(componentModel = "spring", uses = {FileOperationResponseMapper.class})
public interface FileResponseMapper {
    FileResponse resultToResponse(FileResult result);
}
