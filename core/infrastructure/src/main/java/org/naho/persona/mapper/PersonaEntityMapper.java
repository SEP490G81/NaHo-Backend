package org.naho.persona.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.file.mapper.FileIdMapper;
import org.naho.persona.entity.PersonaEntity;
import org.naho.persona.model.Persona;

@Mapper(componentModel = "spring", uses = {
        FileIdMapper.class
})
public interface PersonaEntityMapper {
    @Mapping(target = "avatarFileId", source = "avatarFile.id")
    Persona entityToDomain(PersonaEntity entity);

    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    @Mapping(target = "avatarFile", source = "avatarFileId")
    PersonaEntity domainToEntity(Persona domain);
}
