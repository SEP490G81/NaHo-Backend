package org.naho.question.mapper;

import org.mapstruct.Mapper;
import org.naho.question.entity.GrammarEntity;
import org.naho.question.model.Grammar;

@Mapper(componentModel = "spring")
public interface GrammarEntityMapper {

    Grammar entityToDomain(GrammarEntity entity);
}
