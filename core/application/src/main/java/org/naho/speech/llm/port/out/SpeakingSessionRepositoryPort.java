package org.naho.speech.llm.port.out;

import org.naho.pagination.PageData;
import org.naho.speech.llm.command.SpeakingSessionFilterCommand;
import org.naho.speech.llm.result.ActiveSpeakingSessionResult;
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
            String marugotoLevel,
            String formalityLevel,
            String fullTranscript,
            int totalTurns,
            Double asrConfidence,
            Instant startedAt,
            ScoringResult scoringResult);

    PageData<SpeakingSessionListItemResult> findUserSessions(SpeakingSessionFilterCommand command);

    Optional<SpeakingSessionDetailResult> findSessionDetailByCode(String sessionCode, Long userId);

    void createInProgressSession(
            String sessionCode,
            Long userId,
            Long personaId,
            String topic,
            String marugotoLevel,
            String formalityLevel);

    void saveSessionMessage(
            String sessionCode,
            int turnIndex,
            String senderType,
            String content,
            String correctedText,
            String correctionExplanation,
            String grammarNote,
            String hintForLearner,
            Double pronunciationScore);

    Optional<ActiveSpeakingSessionResult> findActiveSession(Long userId, Long personaId);

    Optional<ActiveSpeakingSessionResult> findActiveSessionByCode(String sessionCode, Long userId);

    void updateSessionTurnAndTranscript(String sessionCode, int totalTurns, String fullTranscript);

    int updateStatusForExpiredSessions(String oldStatus, String newStatus, Instant cutoffTime, Instant endedAt);
}

