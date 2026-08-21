package org.naho.persona.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.file.mapper.FileIdMapper;
import org.naho.persona.entity.PersonaEntity;
import org.naho.persona.model.Persona;

@Mapper(componentModel = "spring", uses = {
        FileIdMapper.class,
        ConversationStyleIdMapper.class
})
public interface PersonaEntityMapper {
    @Mapping(target = "avatarFileId", source = "avatarFile.id")
    @Mapping(target = "suggestedConversationStyleId", source = "suggestedConversationStyle.id")
    Persona entityToDomain(PersonaEntity entity);

    PersonaEntity domainToEntity(Persona domain);
}
