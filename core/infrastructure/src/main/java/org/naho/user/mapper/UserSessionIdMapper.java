package org.naho.user.mapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Mapper;
import org.naho.user.entity.UserSessionEntity;

@Mapper(componentModel = "spring")
public abstract class UserSessionIdMapper {

    @PersistenceContext
    protected EntityManager entityManager;

    public UserSessionEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.getReference(UserSessionEntity.class, id);
    }

    public Long entityToId(UserSessionEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
