package org.naho.question.mapper;

import org.mapstruct.Mapper;
import org.naho.question.model.Vocabulary;
import org.naho.vocabulary.entity.VocabularyEntity;

@Mapper(componentModel = "spring")
public interface VocabularyEntityMapper {
    Vocabulary entityToDomain(VocabularyEntity entity);
}
