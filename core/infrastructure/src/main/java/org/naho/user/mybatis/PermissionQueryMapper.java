package org.naho.user.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PermissionQueryMapper {
    List<String> findAllPermissionCodeByUserId(@Param("userId") Long userId);
}
