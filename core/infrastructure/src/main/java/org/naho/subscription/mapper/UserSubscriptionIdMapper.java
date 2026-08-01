package org.naho.subscription.mapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Mapper;
import org.naho.subscription.entity.UserSubscriptionEntity;

@Mapper(componentModel = "spring")
public abstract class UserSubscriptionIdMapper {

    @PersistenceContext
    protected EntityManager entityManager;

    public UserSubscriptionEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.getReference(UserSubscriptionEntity.class, id);
    }

    public Long entityToId(UserSubscriptionEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
