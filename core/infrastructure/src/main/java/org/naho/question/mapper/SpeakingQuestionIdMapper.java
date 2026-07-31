package org.naho.question.mapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Mapper;
import org.naho.question.entity.SpeakingQuestionEntity;

@Mapper(componentModel = "spring")
public abstract class SpeakingQuestionIdMapper {

    @PersistenceContext
    protected EntityManager entityManager;

    public SpeakingQuestionEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.getReference(SpeakingQuestionEntity.class, id);
    }

    public Long entityToId(SpeakingQuestionEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
