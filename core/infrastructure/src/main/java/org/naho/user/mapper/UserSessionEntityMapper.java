package org.naho.user.mapper;

import org.mapstruct.Mapper;
import org.naho.user.entity.UserSessionEntity;
import org.naho.user.model.UserSession;

@Mapper(componentModel = "spring")
public interface UserSessionEntityMapper {
    UserSessionEntity domainToEntity(UserSession domain);
    
    UserSession entityToDomain(UserSessionEntity entity);
}
