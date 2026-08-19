package org.naho.question.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.question.entity.VocabularyQuestionEntity;
import org.naho.question.model.VocabularyQuestion;

@Mapper(componentModel = "spring", uses = {VocabularyEntityMapper.class})
public interface VocabularyQuestionEntityMapper {

    VocabularyQuestion entityToDomain(VocabularyQuestionEntity entity);

    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    @Mapping(target = "learningPathNode", ignore = true)
    VocabularyQuestionEntity domainToEntity(VocabularyQuestion domain);
}
