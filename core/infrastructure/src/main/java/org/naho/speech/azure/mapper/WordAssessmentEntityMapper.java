package org.naho.speech.azure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.naho.speech.azure.entity.WordAssessmentEntity;
import org.naho.speech.azure.model.WordAssessment;

@Mapper(
        componentModel = "spring",
        nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        uses = {SpeechAssessmentIdMapper.class}
)
public interface WordAssessmentEntityMapper {

    @Mapping(target = "speechAssessmentId", source = "speechAssessment.id")
    WordAssessment entityToDomain(WordAssessmentEntity entity);

    @Mapping(target = "speechAssessment", source = "speechAssessmentId")
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    WordAssessmentEntity domainToEntity(WordAssessment domain);
}

