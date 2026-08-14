package org.naho.cost.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.cost.entity.AwsDailyCostEntity;
import org.naho.cost.model.AwsDailyCost;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AwsDailyCostMapper {

    AwsDailyCost toDomain(AwsDailyCostEntity entity);

    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    AwsDailyCostEntity toEntity(AwsDailyCost domain);

    List<AwsDailyCost> toDomainList(List<AwsDailyCostEntity> entities);
}
