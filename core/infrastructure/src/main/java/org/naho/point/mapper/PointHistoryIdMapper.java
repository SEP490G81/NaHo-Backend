package org.naho.point.mapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Mapper;
import org.naho.point.entity.PointHistoryEntity;

@Mapper(componentModel = "spring")
public abstract class PointHistoryIdMapper {

    @PersistenceContext
    protected EntityManager entityManager;

    public PointHistoryEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.getReference(PointHistoryEntity.class, id);
    }

    public Long entityToId(PointHistoryEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
