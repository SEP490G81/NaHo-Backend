package org.naho.persona.mapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Mapper;
import org.naho.persona.entity.ConversationStyleEntity;

@Mapper(componentModel = "spring")
public abstract class ConversationStyleIdMapper {
    @PersistenceContext
    protected EntityManager entityManager;

    public ConversationStyleEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.getReference(ConversationStyleEntity.class, id);
    }

    public Long entityToId(ConversationStyleEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
