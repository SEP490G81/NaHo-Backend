package org.naho.speech.llm.conversation.mapper;

import org.naho.speech.llm.conversation.port.out.SpeakingSessionMessageRepositoryPort;
import org.naho.speech.llm.conversation.result.SpeakingSessionListItemResult;
import org.naho.speech.llm.conversation.result.SpeakingSessionMessageResult;
import org.naho.speech.llm.conversation.result.SpeakingSessionResult;
import org.naho.speech.llm.model.conversation.SpeakingSession;
import org.naho.speech.llm.model.conversation.SpeakingSessionMessage;

import java.util.List;

public class SpeakingSessionResultMapper {
    private final SpeakingSessionMessageRepositoryPort speakingSessionMessageRepositoryPort;
    private final SpeakingSessionMessageResultMapper speakingSessionMessageResultMapper;

    public SpeakingSessionResultMapper(
            SpeakingSessionMessageRepositoryPort speakingSessionMessageRepositoryPort,
            SpeakingSessionMessageResultMapper speakingSessionMessageResultMapper
    ) {
        this.speakingSessionMessageRepositoryPort = speakingSessionMessageRepositoryPort;
        this.speakingSessionMessageResultMapper = speakingSessionMessageResultMapper;
    }

    public SpeakingSessionResult domainToResult(SpeakingSession domain) {
        if (domain == null) {
            return null;
        }

        List<SpeakingSessionMessage> messages = speakingSessionMessageRepositoryPort.findAllBySessionId(domain.getId());

        List<SpeakingSessionMessageResult> messageResults = messages.stream()
                .map(speakingSessionMessageResultMapper::domainToResult)
                .toList();

        return SpeakingSessionResult.builder()
                .id(domain.getId())
                .sessionCode(domain.getSessionCode())
                .userId(domain.getUserId())
                .personaId(domain.getPersonaId())
                .topic(domain.getTopic())
                .voiceName(domain.getVoiceName())
                .marugotoLevel(domain.getMarugotoLevel())
                .formalityLevel(domain.getFormalityLevel())
                .durationSeconds(domain.getDurationSeconds())
                .totalTurns(domain.getTotalTurns())
                .asrConfidence(domain.getAsrConfidence())
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

        return SpeakingSessionListItemResult.builder()
                .id(domain.getId())
                .sessionCode(domain.getSessionCode())
                .userId(domain.getUserId())
                .personaId(domain.getPersonaId())
                .topic(domain.getTopic())
                .voiceName(domain.getVoiceName())
                .marugotoLevel(domain.getMarugotoLevel())
                .formalityLevel(domain.getFormalityLevel())
                .durationSeconds(domain.getDurationSeconds())
                .totalTurns(domain.getTotalTurns())
                .asrConfidence(domain.getAsrConfidence())
                .status(domain.getStatus())
                .startedAt(domain.getStartedAt())
                .endedAt(domain.getEndedAt())
                .build();
    }
}
