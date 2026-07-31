package org.naho.learning.mapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Mapper;
import org.naho.learning.entity.UserNodeProgressEntity;

@Mapper(componentModel = "spring")
public abstract class UserNodeProgressIdMapper {

    @PersistenceContext
    protected EntityManager entityManager;

    public UserNodeProgressEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.getReference(UserNodeProgressEntity.class, id);
    }

    public Long entityToId(UserNodeProgressEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
