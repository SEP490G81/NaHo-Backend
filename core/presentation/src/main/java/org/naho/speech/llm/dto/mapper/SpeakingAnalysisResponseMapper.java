package org.naho.speech.llm.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.file.dto.mapper.FileResponseMapper;
import org.naho.speech.azure.dto.mapper.WordAssessmentResponseMapper;
import org.naho.speech.llm.conversation.result.SpeakingAnalysisResult;
import org.naho.speech.llm.dto.response.SpeakingAnalysisResponse;

@Mapper(componentModel = "spring", uses = {
        FileResponseMapper.class,
        WordAssessmentResponseMapper.class
})
public interface SpeakingAnalysisResponseMapper {

    @Mapping(target = "audioFile", source = "audioFile")
    SpeakingAnalysisResponse resultToResponse(SpeakingAnalysisResult result);
}
