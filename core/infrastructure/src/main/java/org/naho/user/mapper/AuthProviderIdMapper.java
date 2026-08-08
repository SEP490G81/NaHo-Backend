package org.naho.user.mapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Mapper;
import org.naho.user.entity.AuthProviderEntity;

@Mapper(componentModel = "spring")
public abstract class AuthProviderIdMapper {

    @PersistenceContext
    protected EntityManager entityManager;

    public AuthProviderEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.getReference(AuthProviderEntity.class, id);
    }

    public Long entityToId(AuthProviderEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
