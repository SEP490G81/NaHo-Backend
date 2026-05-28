package org.naho.user.mapper;

import org.mapstruct.Mapper;
import org.naho.user.entity.PermissionEntity;
import org.naho.user.model.Permission;

@Mapper(componentModel = "spring")
public interface PermissionEntityMapper {
    Permission entityToDomain(PermissionEntity entity);
}
