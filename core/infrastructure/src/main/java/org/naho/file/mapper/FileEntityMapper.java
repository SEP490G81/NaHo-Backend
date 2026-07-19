package org.naho.file.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.naho.file.entity.FileEntity;
import org.naho.file.model.File;
import org.naho.point.constant.CloudFrontProperties;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class FileEntityMapper {

    @Autowired
    private CloudFrontProperties cloudFrontProperties;

    @Mapping(target = "commentId", source = "comment.id")
    @Mapping(target = "reportId", source = "report.id")
    @Mapping(target = "objectKey", source = "objectKey", qualifiedByName = "fullObjectKey")
    public abstract File entityToDomain(FileEntity entity);

    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    @Mapping(target = "persona", ignore = true)
    @Mapping(target = "comment", ignore = true)
    @Mapping(target = "speakingQuestion", ignore = true)
    @Mapping(target = "report", ignore = true)
    @Mapping(target = "league", ignore = true)
    @Mapping(target = "user", ignore = true)
    public abstract FileEntity domainToEntity(File domain);

    @Named("fullObjectKey")
    protected String getFullObjectKey(String objectKey) {
        if (objectKey == null) {
            return null;
        }
        return cloudFrontProperties.getDomain() + objectKey;
    }
}
