package org.naho.daily.mapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Mapper;
import org.naho.daily.entity.UserDailyMissionEntity;

@Mapper(componentModel = "spring")
public abstract class UserDailyMissionIdMapper {

    @PersistenceContext
    protected EntityManager entityManager;

    public UserDailyMissionEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.getReference(UserDailyMissionEntity.class, id);
    }

    public Long entityToId(UserDailyMissionEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
