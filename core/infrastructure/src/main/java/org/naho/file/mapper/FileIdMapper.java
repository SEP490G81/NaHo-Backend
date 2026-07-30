package org.naho.file.mapper;

import org.mapstruct.Mapper;
import org.naho.file.entity.FileEntity;

@Mapper(componentModel = "spring")
public interface FileIdMapper {
    default FileEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        FileEntity entity = new FileEntity();
        entity.setId(id);
        return entity;
    }

    default Long entityToId(FileEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
