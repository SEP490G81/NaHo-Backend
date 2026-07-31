package org.naho.user.mapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Mapper;
import org.naho.user.entity.UserEntity;

@Mapper(componentModel = "spring")
public abstract class UserIdMapper {

    @PersistenceContext
    protected EntityManager entityManager;

    public UserEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.getReference(UserEntity.class, id);
    }

    public Long entityToId(UserEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
