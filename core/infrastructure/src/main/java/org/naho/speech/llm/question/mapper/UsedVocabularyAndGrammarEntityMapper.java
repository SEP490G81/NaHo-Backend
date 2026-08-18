package org.naho.speech.llm.question.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.naho.speech.llm.model.question.UsedVocabularyAndGrammar;
import org.naho.speech.llm.question.entity.UsedVocabularyAndGrammarEntity;

@Mapper(
        componentModel = "spring",
        nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        uses = {AiFeedbackIdMapper.class}
)
public interface UsedVocabularyAndGrammarEntityMapper {

    @Mapping(target = "aiFeedbackId", source = "aiFeedback.id")
    UsedVocabularyAndGrammar entityToDomain(UsedVocabularyAndGrammarEntity entity);

    @Mapping(target = "aiFeedback", source = "aiFeedbackId")
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    UsedVocabularyAndGrammarEntity domainToEntity(UsedVocabularyAndGrammar domain);
}
