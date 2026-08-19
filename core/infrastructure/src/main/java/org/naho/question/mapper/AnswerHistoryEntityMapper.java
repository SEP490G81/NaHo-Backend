package org.naho.question.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.file.mapper.FileIdMapper;
import org.naho.question.entity.AnswerHistoryEntity;
import org.naho.speech.azure.mapper.SpeechAssessmentIdMapper;
import org.naho.speech.azure.model.AnswerHistory;
import org.naho.speech.llm.question.mapper.AiFeedbackIdMapper;
import org.naho.user.mapper.UserIdMapper;

@Mapper(componentModel = "spring", uses = {
        AnswerHistoryIdMapper.class,
        SpeechAssessmentIdMapper.class,
        AiFeedbackIdMapper.class,
        FileIdMapper.class,
        UserIdMapper.class,
        SpeakingQuestionIdMapper.class
})
public interface AnswerHistoryEntityMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "speakingQuestionId", source = "speakingQuestion.id")
    @Mapping(target = "audioFileId", source = "audioFile.id")
    @Mapping(target = "speechAssessmentId", source = "speechAssessment.id")
    @Mapping(target = "aiFeedbackId", source = "aiFeedback.id")
    AnswerHistory entityToDomain(AnswerHistoryEntity entity);

    @Mapping(target = "user", source = "userId")
    @Mapping(target = "speakingQuestion", source = "speakingQuestionId")
    @Mapping(target = "audioFile", source = "audioFileId")
    @Mapping(target = "speechAssessment", source = "speechAssessmentId")
    @Mapping(target = "aiFeedback", source = "aiFeedbackId")
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    AnswerHistoryEntity domainToEntity(AnswerHistory domain);
}
