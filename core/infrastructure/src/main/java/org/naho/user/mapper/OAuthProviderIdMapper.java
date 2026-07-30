package org.naho.user.mapper;

import org.mapstruct.Mapper;
import org.naho.user.entity.OAuthProviderEntity;

@Mapper(componentModel = "spring")
public interface OAuthProviderIdMapper {
    default OAuthProviderEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        OAuthProviderEntity entity = new OAuthProviderEntity();
        entity.setId(id);
        return entity;
    }

    default Long entityToId(OAuthProviderEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
