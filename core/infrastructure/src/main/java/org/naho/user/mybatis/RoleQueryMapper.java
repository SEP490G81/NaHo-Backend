package org.naho.user.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.naho.user.entity.RoleEntity;
import org.naho.user.type.RoleName;

import java.util.List;

@Mapper
public interface RoleQueryMapper {
    List<String> findRoleNamesByUserId(@Param("userId") Long userId);

    RoleEntity findByName(@Param("roleName") RoleName roleName);
}
