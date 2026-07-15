package org.naho.season.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.season.entity.LeagueEntity;
import org.naho.season.model.League;

@Mapper(componentModel = "spring")
public interface LeagueEntityMapper {
    @Mapping(target = "iconFileId", source = "iconFile.id")
    League entityToDomain(LeagueEntity entity);
}
