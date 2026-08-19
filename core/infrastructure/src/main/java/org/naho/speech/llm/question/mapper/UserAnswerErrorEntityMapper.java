package org.naho.speech.llm.question.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.naho.speech.llm.model.question.UserAnswerError;
import org.naho.speech.llm.question.entity.UserAnswerErrorEntity;

@Mapper(
        componentModel = "spring",
        nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        uses = {AiFeedbackIdMapper.class}
)
public interface UserAnswerErrorEntityMapper {

    @Mapping(target = "aiFeedbackId", source = "aiFeedback.id")
    UserAnswerError entityToDomain(UserAnswerErrorEntity entity);

    @Mapping(target = "aiFeedback", source = "aiFeedbackId")
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    UserAnswerErrorEntity domainToEntity(UserAnswerError domain);
}
