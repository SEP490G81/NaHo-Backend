package org.naho.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.user.entity.AuthProviderEntity;
import org.naho.user.model.AuthProvider;

@Mapper(componentModel = "spring")
public interface AuthProviderEntityMapper {
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    AuthProviderEntity domainToEntity(AuthProvider domain);

    @Mapping(target = "userId", source = "user.id")
    AuthProvider entityToDomain(AuthProviderEntity entity);
}
