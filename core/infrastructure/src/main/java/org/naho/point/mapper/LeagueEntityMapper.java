package org.naho.point.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.point.entity.LeagueEntity;
import org.naho.point.model.League;

@Mapper(componentModel = "spring")
public interface LeagueEntityMapper {
    @Mapping(target = "iconFileId", source = "iconFile.id")
    League entityToDomain(LeagueEntity entity);
}
