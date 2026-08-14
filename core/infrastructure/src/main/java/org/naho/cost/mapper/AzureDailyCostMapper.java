package org.naho.cost.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.cost.entity.AzureDailyCostEntity;
import org.naho.cost.model.AzureDailyCost;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AzureDailyCostMapper {

    @Mapping(target = "updatedAt", expression = "java(entity.getModifiedTime() != null ? java.time.LocalDateTime.ofInstant(entity.getModifiedTime(), java.time.ZoneOffset.UTC) : (entity.getCreatedTime() != null ? java.time.LocalDateTime.ofInstant(entity.getCreatedTime(), java.time.ZoneOffset.UTC) : null))")
    AzureDailyCost toDomain(AzureDailyCostEntity entity);

    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    AzureDailyCostEntity toEntity(AzureDailyCost domain);

    List<AzureDailyCost> toDomainList(List<AzureDailyCostEntity> entities);

}
