package org.naho.daily.mapper;

import org.mapstruct.Mapper;
import org.naho.daily.entity.DailyMissionEntity;
import org.naho.daily.model.DailyMission;

@Mapper(componentModel = "spring")
public interface DailyMissionEntityMapper {
    DailyMissionEntity domainToEntity(DailyMission domain);

    DailyMission entityToDomain(DailyMissionEntity entity);
}

