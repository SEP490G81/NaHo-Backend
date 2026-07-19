package org.naho.league.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.naho.league.entity.LeagueEntity;

import java.util.List;

@Mapper
public interface LeagueQueryMapper {
    List<LeagueEntity> findAll();
}
