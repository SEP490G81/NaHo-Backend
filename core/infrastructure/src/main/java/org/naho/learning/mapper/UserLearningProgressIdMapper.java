package org.naho.learning.mapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Mapper;
import org.naho.learning.entity.UserLearningProgressEntity;

@Mapper(componentModel = "spring")
public abstract class UserLearningProgressIdMapper {

    @PersistenceContext
    protected EntityManager entityManager;

    public UserLearningProgressEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.getReference(UserLearningProgressEntity.class, id);
    }

    public Long entityToId(UserLearningProgressEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
