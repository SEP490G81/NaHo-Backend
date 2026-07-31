package org.naho.daily.mapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Mapper;
import org.naho.daily.entity.UserDailyAttendanceEntity;

@Mapper(componentModel = "spring")
public abstract class UserDailyAttendanceIdMapper {

    @PersistenceContext
    protected EntityManager entityManager;

    public UserDailyAttendanceEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.getReference(UserDailyAttendanceEntity.class, id);
    }

    public Long entityToId(UserDailyAttendanceEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
