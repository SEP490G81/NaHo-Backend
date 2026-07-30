package org.naho.question.mapper;

import org.mapstruct.Mapper;
import org.naho.question.entity.SpeakingQuestionEntity;

@Mapper(componentModel = "spring")
public interface SpeakingQuestionIdMapper {
    default SpeakingQuestionEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        SpeakingQuestionEntity entity = new SpeakingQuestionEntity();
        entity.setId(id);
        return entity;
    }

    default Long entityToId(SpeakingQuestionEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
