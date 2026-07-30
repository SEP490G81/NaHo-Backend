package org.naho.user.mapper;

import org.mapstruct.Mapper;
import org.naho.user.entity.UserSessionEntity;

@Mapper(componentModel = "spring")
public interface UserSessionIdMapper {
    default UserSessionEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        UserSessionEntity entity = new UserSessionEntity();
        entity.setId(id);
        return entity;
    }

    default Long entityToId(UserSessionEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
