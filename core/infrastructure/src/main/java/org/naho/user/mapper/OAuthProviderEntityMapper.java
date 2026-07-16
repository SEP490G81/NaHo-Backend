package org.naho.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.user.entity.OAuthProviderEntity;
import org.naho.user.model.OAuthProvider;

@Mapper(componentModel = "spring")
public interface OAuthProviderEntityMapper {
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    OAuthProviderEntity domainToEntity(OAuthProvider domain);

    @Mapping(target = "userId", source = "user.id")
    OAuthProvider entityToDomain(OAuthProviderEntity entity);
}
