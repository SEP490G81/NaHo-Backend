package org.naho.speech.llm.port.out;

import org.naho.pagination.PageData;
import org.naho.speech.llm.command.SpeakingSessionFilterCommand;
import org.naho.speech.llm.result.ScoringResult;
import org.naho.speech.llm.result.SpeakingSessionDetailResult;
import org.naho.speech.llm.result.SpeakingSessionListItemResult;

import java.time.Instant;
import java.util.Optional;

public interface SpeakingSessionRepositoryPort {

    void saveSpeakingSession(
            String sessionCode,
            Long userId,
            Long personaId,
            String topic,
            String sessionType,
            String marugotoLevel,
            String formalityLevel,
            String fullTranscript,
            int totalTurns,
            Double asrConfidence,
            Instant startedAt,
            ScoringResult scoringResult);

    PageData<SpeakingSessionListItemResult> findUserSessions(SpeakingSessionFilterCommand command);

    Optional<SpeakingSessionDetailResult> findSessionDetailByCode(String sessionCode, Long userId);
}
