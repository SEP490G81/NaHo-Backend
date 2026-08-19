package org.naho.speech.llm.question.mapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Mapper;
import org.naho.speech.llm.question.entity.AiFeedbackEntity;

@Mapper(componentModel = "spring")
public abstract class AiFeedbackIdMapper {

    @PersistenceContext
    protected EntityManager entityManager;

    public AiFeedbackEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.getReference(AiFeedbackEntity.class, id);
    }

    public Long entityToId(AiFeedbackEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
