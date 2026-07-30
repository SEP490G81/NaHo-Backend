package org.naho.user.mapper;

import org.mapstruct.Mapper;
import org.naho.user.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface UserIdMapper {
    default UserEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        UserEntity entity = new UserEntity();
        entity.setId(id);
        return entity;
    }

    default Long entityToId(UserEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
