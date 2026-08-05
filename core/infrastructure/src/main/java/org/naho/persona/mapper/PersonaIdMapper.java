package org.naho.persona.mapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Mapper;
import org.naho.persona.entity.PersonaEntity;

@Mapper(componentModel = "spring")
public abstract class PersonaIdMapper {

    @PersistenceContext
    protected EntityManager entityManager;

    public PersonaEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.getReference(PersonaEntity.class, id);
    }

    public Long entityToId(PersonaEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
