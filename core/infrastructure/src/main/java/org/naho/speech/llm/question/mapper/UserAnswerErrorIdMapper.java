package org.naho.speech.llm.question.mapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Mapper;
import org.naho.speech.llm.question.entity.UserAnswerErrorEntity;

@Mapper(componentModel = "spring")
public abstract class UserAnswerErrorIdMapper {

    @PersistenceContext
    protected EntityManager entityManager;

    public UserAnswerErrorEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.getReference(UserAnswerErrorEntity.class, id);
    }

    public Long entityToId(UserAnswerErrorEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
