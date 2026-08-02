package org.naho.file.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.file.dto.response.FileOperationResponse;
import org.naho.file.result.FileOperationResult;

@Mapper(componentModel = "spring")
public interface FileOperationResponseMapper {
    FileOperationResponse resultToResponse(FileOperationResult result);
}
