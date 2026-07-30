package org.naho.point.mapper;

import org.mapstruct.Mapper;
import org.naho.point.entity.PointHistoryEntity;

@Mapper(componentModel = "spring")
public interface PointHistoryIdMapper {
    default PointHistoryEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        PointHistoryEntity entity = new PointHistoryEntity();
        entity.setId(id);
        return entity;
    }

    default Long entityToId(PointHistoryEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
