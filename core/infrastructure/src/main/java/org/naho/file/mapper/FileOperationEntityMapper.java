package org.naho.file.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.file.entity.FileOperationEntity;
import org.naho.file.model.FileOperation;

@Mapper(componentModel = "spring", uses = FileIdMapper.class)
public interface FileOperationEntityMapper {
    @Mapping(target = "file", source = "fileId")
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    FileOperationEntity domainToEntity(FileOperation domain);

    @Mapping(target = "fileId", source = "file.id")
    FileOperation entityToDomain(FileOperationEntity entity);
}
