package org.naho.daily.mapper;

import org.mapstruct.Mapper;
import org.naho.daily.entity.UserDailyMissionEntity;

@Mapper(componentModel = "spring")
public interface UserDailyMissionIdMapper {
    default UserDailyMissionEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        UserDailyMissionEntity entity = new UserDailyMissionEntity();
        entity.setId(id);
        return entity;
    }

    default Long entityToId(UserDailyMissionEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
