package org.naho.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.file.mapper.FileEntityMapper;
import org.naho.user.entity.UserEntity;
import org.naho.user.model.User;

@Mapper(
        componentModel = "spring",
        uses = {
                RoleEntityMapper.class,
                FileEntityMapper.class,
                UserValueObjectMapper.class
        }
)
public interface UserEntityMapper {
    @Mapping(target = "avatarFileId", source = "avatarFile.id")
    @Mapping(target = "roleIds", source = "roles")
    @Mapping(target = "userSessionIds", ignore = true)
    User entityToDomain(UserEntity entity);


    @Mapping(target = "avatarFile", ignore = true)
    @Mapping(target = "roles", source = "roleIds")
    @Mapping(target = "jlptLevel", defaultValue = "N5")
    UserEntity domainToEntity(User user);
}
