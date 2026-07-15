package org.naho.season.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.season.entity.SeasonEntity;
import org.naho.season.model.Season;

@Mapper(componentModel = "spring")
public interface SeasonEntityMapper {

    @Mapping(target = "userSeasonPoints", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    SeasonEntity domainToEntity(Season domain);
    
    Season entityToDomain(SeasonEntity entity);
}
