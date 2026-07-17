package org.naho.league.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.league.entity.LeagueEntity;
import org.naho.league.model.League;

@Mapper(componentModel = "spring")
public interface LeagueEntityMapper {
    @Mapping(target = "iconFileId", source = "iconFile.id")
    League entityToDomain(LeagueEntity entity);
}
