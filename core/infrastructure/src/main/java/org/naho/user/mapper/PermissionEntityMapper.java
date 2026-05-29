package org.naho.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.user.entity.PermissionEntity;
import org.naho.user.model.Permission;

@Mapper(componentModel = "spring")
public interface PermissionEntityMapper {
    @Mapping(target = "roleIds", ignore = true)
    Permission entityToDomain(PermissionEntity entity);
}
