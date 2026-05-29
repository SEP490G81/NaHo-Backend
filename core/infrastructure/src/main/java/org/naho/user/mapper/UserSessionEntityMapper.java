package org.naho.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.user.entity.UserSessionEntity;
import org.naho.user.model.UserSession;

@Mapper(componentModel = "spring")
public interface UserSessionEntityMapper {
    @Mapping(target = "user", ignore = true)
    UserSessionEntity domainToEntity(UserSession domain);

    @Mapping(target = "userId", source = "user.id")
    UserSession entityToDomain(UserSessionEntity entity);
}
