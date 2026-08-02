package org.naho.file.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.file.entity.FileEntity;
import org.naho.file.model.File;

@Mapper(componentModel = "spring")
public interface FileEntityMapper {
    @Mapping(target = "commentId", source = "comment.id")
    @Mapping(target = "reportId", source = "report.id")
    File entityToDomain(FileEntity entity);

    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    @Mapping(target = "persona", ignore = true)
    @Mapping(target = "comment", ignore = true)
    @Mapping(target = "speakingQuestion", ignore = true)
    @Mapping(target = "report", ignore = true)
    @Mapping(target = "league", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "fileOperation", ignore = true)
    FileEntity domainToEntity(File domain);
}
