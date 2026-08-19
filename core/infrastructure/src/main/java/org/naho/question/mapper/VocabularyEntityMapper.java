package org.naho.question.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.question.model.Vocabulary;
import org.naho.vocabulary.entity.VocabularyEntity;

@Mapper(componentModel = "spring")
public interface VocabularyEntityMapper {
    Vocabulary entityToDomain(VocabularyEntity entity);

    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    @Mapping(target = "vocabularyQuestions", ignore = true)
    @Mapping(target = "speakingQuestions", ignore = true)
    VocabularyEntity domainToEntity(Vocabulary domain);
}
