package org.naho.season.mybatis;

import org.apache.ibatis.annotations.Mapper;

import java.time.Instant;

@Mapper
public interface SeasonQueryMapper {
    Integer findLatestSeasonNo();

    boolean existsOverlappingSeason(Instant startAt, Instant endAt);
}
