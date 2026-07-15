package org.naho.season.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.naho.season.entity.LeagueEntity;

import java.util.List;

@Mapper
public interface LeagueQueryMapper {
    List<LeagueEntity> findAll();
}
