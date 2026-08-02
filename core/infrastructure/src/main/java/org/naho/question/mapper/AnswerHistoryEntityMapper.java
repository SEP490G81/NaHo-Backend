package org.naho.question.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.file.mapper.FileIdMapper;
import org.naho.question.entity.AnswerHistoryEntity;
import org.naho.speech.model.AnswerHistory;
import org.naho.user.mapper.UserIdMapper;

@Mapper(componentModel = "spring", uses = {
        UserIdMapper.class,
        SpeakingQuestionIdMapper.class,
        FileIdMapper.class
})
public interface AnswerHistoryEntityMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "speakingQuestionId", source = "speakingQuestion.id")
    @Mapping(target = "audioFileId", source = "audioFile.id")
    AnswerHistory entityToDomain(AnswerHistoryEntity entity);

    @Mapping(target = "user", source = "userId")
    @Mapping(target = "speakingQuestion", source = "speakingQuestionId")
    @Mapping(target = "audioFile", source = "audioFileId")
    @Mapping(target = "contentAssessment", ignore = true)
    @Mapping(target = "speechAssessment", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    AnswerHistoryEntity domainToEntity(AnswerHistory domain);
}
