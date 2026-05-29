package org.naho.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.user.entity.RoleEntity;
import org.naho.user.model.Role;

@Mapper(
        componentModel = "spring",
        uses = {PermissionEntityMapper.class}
)
public interface RoleEntityMapper {
    @Mapping(target = "permissionIds", ignore = true)
    @Mapping(target = "userIds", ignore = true)
    Role entityToDomain(RoleEntity entity);

    default Long entityToId(RoleEntity role) {
        return role == null ? null : role.getId();
    }

    default org.naho.user.entity.RoleEntity idToRoleEntity(Long id) {
        if (id == null) {
            return null;
        }
        org.naho.user.entity.RoleEntity roleEntity = new org.naho.user.entity.RoleEntity();
        roleEntity.setId(id);
        return roleEntity;
    }
}

