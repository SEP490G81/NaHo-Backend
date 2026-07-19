package org.naho.question.mapper;

import org.mapstruct.Mapper;
import org.naho.vocabulary.entity.VocabularyEntity;
import org.naho.vocabulary.model.Vocabulary;

@Mapper(componentModel = "spring")
public interface VocabularyEntityMapper {
    Vocabulary entityToDomain(VocabularyEntity entity);
}
