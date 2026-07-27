package org.naho.daily.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.daily.entity.DailyMissionEntity;
import org.naho.daily.model.DailyMission;

@Mapper(componentModel = "spring")
public interface DailyMissionEntityMapper {
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    @Mapping(target = "userDailyMissions", ignore = true)
    DailyMissionEntity domainToEntity(DailyMission domain);

    DailyMission entityToDomain(DailyMissionEntity entity);
}

