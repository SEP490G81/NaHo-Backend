package org.naho.speech.llm.question.mapper;

import org.mapstruct.*;
import org.naho.speech.llm.model.question.AiFeedback;
import org.naho.speech.llm.question.entity.AiFeedbackEntity;

@Mapper(
        componentModel = "spring",
        nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        uses = {
                UsedVocabularyAndGrammarEntityMapper.class,
                UserAnswerErrorEntityMapper.class
        }
)
public interface AiFeedbackEntityMapper {

    AiFeedback entityToDomain(AiFeedbackEntity entity);

    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    AiFeedbackEntity domainToEntity(AiFeedback domain);

    @AfterMapping
    default void linkAiFeedback(@MappingTarget AiFeedbackEntity entity) {
        if (entity.getUsedVocabulariesAndGrammars() != null) {
            entity.getUsedVocabulariesAndGrammars().forEach(v -> v.setAiFeedback(entity));
        }
        if (entity.getUserAnswerErrors() != null) {
            entity.getUserAnswerErrors().forEach(e -> e.setAiFeedback(entity));
        }
    }
}
