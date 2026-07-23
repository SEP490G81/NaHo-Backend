package org.naho.chest.mapper;

import org.mapstruct.Mapper;
import org.naho.chest.entity.ChestEntity;
import org.naho.chest.model.Chest;

@Mapper(componentModel = "spring")
public interface ChestEntityMapper {
    Chest entityToDomain(ChestEntity entity);
}
