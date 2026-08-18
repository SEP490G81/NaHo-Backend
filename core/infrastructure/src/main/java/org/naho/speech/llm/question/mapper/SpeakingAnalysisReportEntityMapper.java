package org.naho.speech.llm.question.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.naho.file.mapper.FileIdMapper;
import org.naho.question.mapper.AnswerHistoryIdMapper;
import org.naho.speech.azure.mapper.SpeechAssessmentIdMapper;
import org.naho.speech.llm.model.question.SpeakingAnalysis;
import org.naho.speech.llm.question.entity.SpeakingAnalysisReportEntity;

@Mapper(
        componentModel = "spring",
        nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        uses = {
                SpeechAssessmentIdMapper.class,
                AiFeedbackIdMapper.class,
                AnswerHistoryIdMapper.class,
                FileIdMapper.class
        }
)
public interface SpeakingAnalysisReportEntityMapper {

    @Mapping(target = "speechAssessmentId", source = "speechAssessment.id")
    @Mapping(target = "aiFeedbackId", source = "aiFeedback.id")
    @Mapping(target = "answerHistoryId", source = "answerHistory.id")
    @Mapping(target = "audioFileId", source = "audioFile.id")
    SpeakingAnalysis entityToDomain(SpeakingAnalysisReportEntity entity);

    @Mapping(target = "speechAssessment", source = "speechAssessmentId")
    @Mapping(target = "aiFeedback", source = "aiFeedbackId")
    @Mapping(target = "answerHistory", source = "answerHistoryId")
    @Mapping(target = "audioFile", source = "audioFileId")
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    SpeakingAnalysisReportEntity domainToEntity(SpeakingAnalysis domain);
}
