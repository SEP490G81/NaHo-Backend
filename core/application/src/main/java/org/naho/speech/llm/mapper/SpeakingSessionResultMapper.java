package org.naho.speech.llm.mapper;

import org.naho.speech.llm.model.SpeakingSession;
import org.naho.speech.llm.model.SpeakingSessionMessage;
import org.naho.speech.llm.port.out.SpeakingSessionMessageRepositoryPort;
import org.naho.speech.llm.result.SpeakingSessionMessageResult;
import org.naho.speech.llm.result.SpeakingSessionResult;

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
                .fullTranscript(domain.getFullTranscript())
                .status(domain.getStatus())
                .startedAt(domain.getStartedAt())
                .endedAt(domain.getEndedAt())
                .messages(messageResults)
                .build();
    }
}
