package org.naho.speech.llm.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.speech.llm.dto.response.ScoringResponse;
import org.naho.speech.llm.result.ScoringResult;

@Mapper(componentModel = "spring")
public interface ScoringResponseMapper {

    @Mapping(target = "scores", source = "result")
    ScoringResponse resultToResponse(ScoringResult result);

    @Mapping(target = "fluency", source = "fluencyScore")
    @Mapping(target = "pronunciation", source = "pronunciationScore")
    @Mapping(target = "grammar", source = "grammarScore")
    @Mapping(target = "vocabulary", source = "vocabularyScore")
    @Mapping(target = "interaction", source = "interactionScore")
    @Mapping(target = "naturalness", source = "naturalnessScore")
    @Mapping(target = "coherence", source = "coherenceScore")
    ScoringResponse.Scores toScores(ScoringResult result);

    ScoringResponse.ImprovedExpression resultToResponseItem(ScoringResult.ImprovedExpression resultItem);
}
