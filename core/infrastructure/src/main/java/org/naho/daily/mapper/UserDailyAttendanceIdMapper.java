package org.naho.daily.mapper;

import org.mapstruct.Mapper;
import org.naho.daily.entity.UserDailyAttendanceEntity;

@Mapper(componentModel = "spring")
public interface UserDailyAttendanceIdMapper {
    default UserDailyAttendanceEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        UserDailyAttendanceEntity entity = new UserDailyAttendanceEntity();
        entity.setId(id);
        return entity;
    }

    default Long entityToId(UserDailyAttendanceEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
