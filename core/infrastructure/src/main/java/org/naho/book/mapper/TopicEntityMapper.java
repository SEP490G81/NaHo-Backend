package org.naho.book.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.book.entity.TopicEntity;
import org.naho.book.model.Topic;

@Mapper(componentModel = "spring")
public interface TopicEntityMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "coverImageFileId", source = "coverImageFile.id")
    Topic entityToDomain(TopicEntity entity);
}
