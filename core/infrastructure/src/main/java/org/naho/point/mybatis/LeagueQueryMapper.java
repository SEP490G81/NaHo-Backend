package org.naho.point.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.naho.point.entity.LeagueEntity;

import java.util.List;

@Mapper
public interface LeagueQueryMapper {
    List<LeagueEntity> findAll();
}
