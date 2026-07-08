package org.naho.file.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.file.model.File;
import org.naho.file.model.FileEntity;

@Mapper(componentModel = "spring")
public interface FileEntityMapper {

    @Mapping(target = "commentId", source = "comment.id")
    @Mapping(target = "questionId", source = "question.id")
    @Mapping(target = "reportId", source = "report.id")
    File entityToDomain(FileEntity entity);

    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    @Mapping(target = "persona", ignore = true)
    @Mapping(target = "comment", ignore = true)
    @Mapping(target = "question", ignore = true)
    @Mapping(target = "report", ignore = true)
    FileEntity domainToEntity(File domain);
}
