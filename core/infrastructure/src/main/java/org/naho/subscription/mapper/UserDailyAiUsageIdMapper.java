package org.naho.subscription.mapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Mapper;
import org.naho.subscription.entity.UserDailyAiUsageEntity;

@Mapper(componentModel = "spring")
public abstract class UserDailyAiUsageIdMapper {

    @PersistenceContext
    protected EntityManager entityManager;

    public UserDailyAiUsageEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.getReference(UserDailyAiUsageEntity.class, id);
    }

    public Long entityToId(UserDailyAiUsageEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
