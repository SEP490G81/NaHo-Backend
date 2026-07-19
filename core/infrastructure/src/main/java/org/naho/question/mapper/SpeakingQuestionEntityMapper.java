package org.naho.question.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.question.entity.SpeakingQuestionEntity;
import org.naho.question.model.SpeakingQuestion;

@Mapper(componentModel = "spring")
public interface SpeakingQuestionEntityMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "speakingQuestionAudioFileId", source = "speakingQuestionAudioFile.id")
    SpeakingQuestion entityToDomain(SpeakingQuestionEntity entity);
}
