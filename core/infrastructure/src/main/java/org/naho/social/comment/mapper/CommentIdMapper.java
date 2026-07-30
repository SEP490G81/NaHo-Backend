package org.naho.social.comment.mapper;

import org.mapstruct.Mapper;
import org.naho.social.entity.CommentEntity;

@Mapper(componentModel = "spring")
public interface CommentIdMapper {
    default CommentEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        CommentEntity entity = new CommentEntity();
        entity.setId(id);
        return entity;
    }

    default Long entityToId(CommentEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
