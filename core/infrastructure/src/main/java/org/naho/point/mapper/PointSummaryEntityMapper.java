package org.naho.point.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.point.entity.PointSummaryEntity;
import org.naho.point.model.PointSummary;

@Mapper(componentModel = "spring")
public interface PointSummaryEntityMapper {
    @Mapping(target = "userId", source = "user.id")
    PointSummary entityToDomain(PointSummaryEntity entity);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    PointSummaryEntity domainToEntity(PointSummary domain);
}
