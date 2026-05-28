package org.naho.user.mapper;

import org.mapstruct.Mapper;
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
    User entityToDomain(UserEntity entity);
}
