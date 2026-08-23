package org.naho.speech.llm.conversation.mapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Mapper;
import org.naho.speech.llm.conversation.entity.SpeakingSessionEntity;

@Mapper(componentModel = "spring")
public abstract class SpeakingSessionIdMapper {

    @PersistenceContext
    protected EntityManager entityManager;

    public SpeakingSessionEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.getReference(SpeakingSessionEntity.class, id);
    }

    public Long entityToId(SpeakingSessionEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
