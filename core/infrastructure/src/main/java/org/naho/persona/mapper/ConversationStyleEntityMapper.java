package org.naho.persona.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.persona.entity.ConversationStyleEntity;
import org.naho.persona.model.ConversationStyle;

@Mapper(componentModel = "spring")
public interface ConversationStyleEntityMapper {
    ConversationStyle entityToDomain(ConversationStyleEntity entity);

    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    @Mapping(target = "personas", ignore = true)
    ConversationStyleEntity domainToEntity(ConversationStyle domain);
}
