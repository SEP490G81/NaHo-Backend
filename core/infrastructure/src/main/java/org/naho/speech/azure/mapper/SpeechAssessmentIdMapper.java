package org.naho.speech.azure.mapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Mapper;
import org.naho.speech.azure.entity.SpeechAssessmentEntity;

@Mapper(componentModel = "spring")
public abstract class SpeechAssessmentIdMapper {

    @PersistenceContext
    protected EntityManager entityManager;

    public SpeechAssessmentEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.getReference(SpeechAssessmentEntity.class, id);
    }

    public Long entityToId(SpeechAssessmentEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
