package org.naho.user.mapper;

import org.mapstruct.Mapper;
import org.naho.user.entity.RoleEntity;

@Mapper(componentModel = "spring")
public interface RoleIdMapper {
    default RoleEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        RoleEntity entity = new RoleEntity();
        entity.setId(id);
        return entity;
    }

    default Long entityToId(RoleEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
