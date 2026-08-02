package org.naho.user.mapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Mapper;
import org.naho.user.entity.RoleEntity;

@Mapper(componentModel = "spring")
public abstract class RoleIdMapper {

    @PersistenceContext
    protected EntityManager entityManager;

    public RoleEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.getReference(RoleEntity.class, id);
    }

    public Long entityToId(RoleEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
