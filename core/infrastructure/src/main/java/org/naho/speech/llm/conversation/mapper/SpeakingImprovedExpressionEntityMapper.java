package org.naho.speech.llm.conversation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.naho.speech.llm.conversation.entity.SpeakingImprovedExpressionEntity;
import org.naho.speech.llm.model.conversation.SpeakingImprovedExpression;

@Mapper(
        componentModel = "spring",
        nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        uses = {
                SpeakingSessionAssessmentIdMapper.class
        }
)
public interface SpeakingImprovedExpressionEntityMapper {

    @Mapping(target = "speakingSessionAssessmentId", source = "speakingSessionAssessment.id")
    SpeakingImprovedExpression entityToDomain(SpeakingImprovedExpressionEntity entity);

    @Mapping(target = "speakingSessionAssessment", source = "speakingSessionAssessmentId")
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    SpeakingImprovedExpressionEntity domainToEntity(SpeakingImprovedExpression domain);
}
