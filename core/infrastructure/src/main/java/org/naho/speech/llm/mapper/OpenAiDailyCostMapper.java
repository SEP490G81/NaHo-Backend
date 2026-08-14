package org.naho.speech.llm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.speech.llm.entity.OpenAiDailyCostEntity;
import org.naho.speech.llm.model.OpenAiDailyCost;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OpenAiDailyCostMapper {

    @Mapping(target = "updatedAt", expression = "java(entity.getModifiedTime() != null ? java.time.LocalDateTime.ofInstant(entity.getModifiedTime(), java.time.ZoneOffset.UTC) : (entity.getCreatedTime() != null ? java.time.LocalDateTime.ofInstant(entity.getCreatedTime(), java.time.ZoneOffset.UTC) : null))")
    OpenAiDailyCost toDomain(OpenAiDailyCostEntity entity);

    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    OpenAiDailyCostEntity toEntity(OpenAiDailyCost domain);

    List<OpenAiDailyCost> toDomainList(List<OpenAiDailyCostEntity> entities);

    List<OpenAiDailyCostEntity> toEntityList(List<OpenAiDailyCost> domains);
}
