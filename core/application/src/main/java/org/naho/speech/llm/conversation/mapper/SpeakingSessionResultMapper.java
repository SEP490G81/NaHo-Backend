package org.naho.speech.llm.conversation.mapper;

import org.naho.persona.port.in.GetPersonaInputPort;
import org.naho.persona.result.PersonaResult;
import org.naho.speech.llm.conversation.port.out.SpeakingSessionMessageRepositoryPort;
import org.naho.speech.llm.conversation.result.SpeakingSessionAssessmentResult;
import org.naho.speech.llm.conversation.result.SpeakingSessionListItemResult;
import org.naho.speech.llm.conversation.result.SpeakingSessionMessageResult;
import org.naho.speech.llm.conversation.result.SpeakingSessionResult;
import org.naho.speech.llm.model.conversation.SpeakingSession;
import org.naho.speech.llm.model.conversation.SpeakingSessionMessage;

import java.util.List;

public class SpeakingSessionResultMapper {
    private final SpeakingSessionMessageRepositoryPort speakingSessionMessageRepositoryPort;
    private final SpeakingSessionMessageResultMapper speakingSessionMessageResultMapper;
    private final GetPersonaInputPort getPersonaInputPort;

    public SpeakingSessionResultMapper(
            SpeakingSessionMessageRepositoryPort speakingSessionMessageRepositoryPort,
            SpeakingSessionMessageResultMapper speakingSessionMessageResultMapper,
            GetPersonaInputPort getPersonaInputPort
    ) {
        this.speakingSessionMessageRepositoryPort = speakingSessionMessageRepositoryPort;
        this.speakingSessionMessageResultMapper = speakingSessionMessageResultMapper;
        this.getPersonaInputPort = getPersonaInputPort;
    }

    public SpeakingSessionResult domainToResult(SpeakingSession domain) {
        if (domain == null) {
            return null;
        }

        List<SpeakingSessionMessage> messages = speakingSessionMessageRepositoryPort.findAllBySessionId(domain.getId());

        List<SpeakingSessionMessageResult> messageResults = messages.stream()
                .map(speakingSessionMessageResultMapper::domainToResult)
                .toList();

        PersonaResult personaResult = getPersonaInputPort.findById(domain.getPersonaId());

        return SpeakingSessionResult.builder()
                .id(domain.getId())
                .sessionCode(domain.getSessionCode())
                .userId(domain.getUserId())
                .persona(personaResult)
                .topic(domain.getTopic())
                .voiceName(domain.getVoiceName())
                .marugotoLevel(domain.getMarugotoLevel())
                .formalityLevel(domain.getFormalityLevel())
                .totalTurns(domain.getTotalTurns())
                .status(domain.getStatus())
                .startedAt(domain.getStartedAt())
                .endedAt(domain.getEndedAt())
                .speakingSessionMessages(messageResults)
                .build();
    }

    public SpeakingSessionListItemResult domainToListItemResult(SpeakingSession domain) {
        if (domain == null) {
            return null;
        }

        PersonaResult personaResult = getPersonaInputPort.findById(domain.getPersonaId());

        return SpeakingSessionListItemResult.builder()
                .id(domain.getId())
                .sessionCode(domain.getSessionCode())
                .userId(domain.getUserId())
                .persona(personaResult)
                .topic(domain.getTopic())
                .voiceName(domain.getVoiceName())
                .marugotoLevel(domain.getMarugotoLevel())
                .formalityLevel(domain.getFormalityLevel())
                .totalTurns(domain.getTotalTurns())
                .status(domain.getStatus())
                .startedAt(domain.getStartedAt())
                .endedAt(domain.getEndedAt())
                .build();
    }

    public SpeakingSessionResult domainToResult(
            SpeakingSession domain,
            SpeakingSessionAssessmentResult speakingSessionAssessmentResult
    ) {
        if (domain == null) {
            return null;
        }

        PersonaResult personaResult = getPersonaInputPort.findById(domain.getPersonaId());

        return SpeakingSessionResult.builder()
                .id(domain.getId())
                .sessionCode(domain.getSessionCode())
                .userId(domain.getUserId())
                .persona(personaResult)
                .topic(domain.getTopic())
                .voiceName(domain.getVoiceName())
                .marugotoLevel(domain.getMarugotoLevel())
                .formalityLevel(domain.getFormalityLevel())
                .totalTurns(domain.getTotalTurns())
                .status(domain.getStatus())
                .startedAt(domain.getStartedAt())
                .endedAt(domain.getEndedAt())
                .speakingSessionAssessment(speakingSessionAssessmentResult)
                .build();
    }
}
