package org.naho.speech.llm.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.file.dto.mapper.FileResponseMapper;
import org.naho.speech.llm.dto.response.SpeakingAnalysisResponse;
import org.naho.speech.llm.result.SpeakingAnalysisResult;

@Mapper(componentModel = "spring", uses = {
        FileResponseMapper.class
})
public interface SpeakingAnalysisResponseMapper {

    @Mapping(target = "audioFile", source = "audioFile")
    SpeakingAnalysisResponse resultToResponse(SpeakingAnalysisResult result);
}
