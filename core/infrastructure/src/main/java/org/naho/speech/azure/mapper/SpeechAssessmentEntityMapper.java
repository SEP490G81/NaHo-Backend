package org.naho.speech.azure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.naho.speech.azure.entity.SpeechAssessmentEntity;
import org.naho.speech.azure.model.SpeechAssessment;

@Mapper(
        componentModel = "spring",
        nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        uses = {
                WordAssessmentEntityMapper.class
        }
)
public interface SpeechAssessmentEntityMapper {
    SpeechAssessment entityToDomain(SpeechAssessmentEntity entity);

    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    SpeechAssessmentEntity domainToEntity(SpeechAssessment domain);
}

