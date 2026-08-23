package org.naho.speech.llm.conversation.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.persona.dto.mapper.PersonaResponseMapper;
import org.naho.speech.llm.conversation.dto.response.SpeakingSessionListItemResponse;
import org.naho.speech.llm.conversation.dto.response.SpeakingSessionResponse;
import org.naho.speech.llm.conversation.result.SpeakingSessionListItemResult;
import org.naho.speech.llm.conversation.result.SpeakingSessionResult;

@Mapper(componentModel = "spring", uses = {
        SpeakingSessionAssessmentResponseMapper.class,
        SpeakingSessionMessageResponseMapper.class,
        PersonaResponseMapper.class
})
public interface SpeakingSessionResponseMapper {
    SpeakingSessionResponse resultToResponse(SpeakingSessionResult result);

    SpeakingSessionListItemResponse resultToResponse(SpeakingSessionListItemResult result);
}
