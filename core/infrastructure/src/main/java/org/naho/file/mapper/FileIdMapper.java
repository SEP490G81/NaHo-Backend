package org.naho.file.mapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Mapper;
import org.naho.file.entity.FileEntity;

@Mapper(componentModel = "spring")
public abstract class FileIdMapper {

    @PersistenceContext
    protected EntityManager entityManager;

    public FileEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.getReference(FileEntity.class, id);
    }

    public Long entityToId(FileEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
