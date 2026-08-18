package org.naho.question.mapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Mapper;
import org.naho.question.entity.AnswerHistoryEntity;

@Mapper(componentModel = "spring")
public abstract class AnswerHistoryIdMapper {

    @PersistenceContext
    protected EntityManager entityManager;

    public AnswerHistoryEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.getReference(AnswerHistoryEntity.class, id);
    }

    public Long entityToId(AnswerHistoryEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
