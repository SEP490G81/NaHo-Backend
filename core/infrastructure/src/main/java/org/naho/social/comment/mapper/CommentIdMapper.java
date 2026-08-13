package org.naho.social.comment.mapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Mapper;
import org.naho.social.comment.entity.CommentEntity;

@Mapper(componentModel = "spring")
public abstract class CommentIdMapper {

    @PersistenceContext
    protected EntityManager entityManager;

    public CommentEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.getReference(CommentEntity.class, id);
    }

    public Long entityToId(CommentEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
