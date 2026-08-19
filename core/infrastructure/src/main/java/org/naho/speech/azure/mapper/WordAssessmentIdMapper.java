package org.naho.speech.azure.mapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Mapper;
import org.naho.speech.azure.entity.WordAssessmentEntity;

@Mapper(componentModel = "spring")
public abstract class WordAssessmentIdMapper {

    @PersistenceContext
    protected EntityManager entityManager;

    public WordAssessmentEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.getReference(WordAssessmentEntity.class, id);
    }

    public Long entityToId(WordAssessmentEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
