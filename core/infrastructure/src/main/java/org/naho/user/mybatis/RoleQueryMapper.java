package org.naho.user.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.naho.user.model.Role;
import org.naho.user.type.RoleName;

import java.util.List;
import java.util.Optional;

@Mapper
public interface RoleQueryMapper {
    List<String> findRoleNamesByUserId(@Param("userId") Long userId);

    Optional<Role> findByName(@Param("roleName") RoleName roleName);
}
