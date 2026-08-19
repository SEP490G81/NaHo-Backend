package org.naho.learning.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.learning.dto.response.LearningPathNodeDetailResponse;
import org.naho.learning.result.LearningPathNodeDetailResult;
import org.naho.question.dto.mapper.SpeakingQuestionResponseMapper;
import org.naho.question.dto.mapper.VocabularyQuestionResponseMapper;

@Mapper(componentModel = "spring", uses = {
        SpeakingQuestionResponseMapper.class,
        VocabularyQuestionResponseMapper.class,
})
public interface LearningPathNodeResponseMapper {
    LearningPathNodeDetailResponse resultToResponse(LearningPathNodeDetailResult result);
}
