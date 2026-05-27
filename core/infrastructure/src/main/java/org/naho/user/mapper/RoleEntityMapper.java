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
    @Mapping(target = "users", ignore = true)
    Role entityToDomain(RoleEntity entity);
}
