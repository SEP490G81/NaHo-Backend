package org.naho.file.mapper;

import org.mapstruct.Mapper;
import org.naho.file.model.File;
import org.naho.file.model.FileEntity;

@Mapper(componentModel = "spring")
public interface FileEntityMapper {
    File entityToDomain(FileEntity entity);

    FileEntity domainToEntity(File domain);
}
