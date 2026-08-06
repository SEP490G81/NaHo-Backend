package org.naho.user.mapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Mapper;
import org.naho.user.entity.OAuthProviderEntity;

@Mapper(componentModel = "spring")
public abstract class OAuthProviderIdMapper {

    @PersistenceContext
    protected EntityManager entityManager;

    public OAuthProviderEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.getReference(OAuthProviderEntity.class, id);
    }

    public Long entityToId(OAuthProviderEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
