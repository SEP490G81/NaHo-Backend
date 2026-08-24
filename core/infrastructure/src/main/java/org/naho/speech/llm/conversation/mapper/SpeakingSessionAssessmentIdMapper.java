package org.naho.speech.llm.conversation.mapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Mapper;
import org.naho.speech.llm.conversation.entity.SpeakingSessionAssessmentEntity;

@Mapper(componentModel = "spring")
public abstract class SpeakingSessionAssessmentIdMapper {

    @PersistenceContext
    protected EntityManager entityManager;

    public SpeakingSessionAssessmentEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.getReference(SpeakingSessionAssessmentEntity.class, id);
    }

    public Long entityToId(SpeakingSessionAssessmentEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
