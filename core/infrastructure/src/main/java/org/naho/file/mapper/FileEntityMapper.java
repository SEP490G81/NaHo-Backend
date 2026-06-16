package org.naho.file.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.file.model.File;
import org.naho.file.model.FileEntity;

@Mapper(componentModel = "spring")
public interface FileEntityMapper {
    File entityToDomain(FileEntity entity);

    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    FileEntity domainToEntity(File domain);
}
