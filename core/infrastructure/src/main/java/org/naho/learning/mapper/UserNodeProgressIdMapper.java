package org.naho.learning.mapper;

import org.mapstruct.Mapper;
import org.naho.learning.entity.UserNodeProgressEntity;

@Mapper(componentModel = "spring")
public interface UserNodeProgressIdMapper {
    default UserNodeProgressEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        UserNodeProgressEntity entity = new UserNodeProgressEntity();
        entity.setId(id);
        return entity;
    }

    default Long entityToId(UserNodeProgressEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
