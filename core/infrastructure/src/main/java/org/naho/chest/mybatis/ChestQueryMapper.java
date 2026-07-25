package org.naho.chest.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.naho.chest.entity.ChestEntity;

import java.util.Optional;

@Mapper
public interface ChestQueryMapper {
    Optional<ChestEntity> findByDailyRewardId(@Param("dailyRewardId") Long dailyRewardId);
}
