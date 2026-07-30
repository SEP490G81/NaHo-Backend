package org.naho.learning.mapper;

import org.mapstruct.Mapper;
import org.naho.learning.entity.UserLearningProgressEntity;

@Mapper(componentModel = "spring")
public interface UserLearningProgressIdMapper {
    default UserLearningProgressEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        UserLearningProgressEntity entity = new UserLearningProgressEntity();
        entity.setId(id);
        return entity;
    }

    default Long entityToId(UserLearningProgressEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
