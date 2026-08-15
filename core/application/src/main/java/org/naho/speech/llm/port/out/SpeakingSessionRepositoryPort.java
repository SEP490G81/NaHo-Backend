package org.naho.speech.llm.port.out;

import org.naho.file.model.File;
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

    void saveSessionMessage(
            String sessionCode,
            int turnIndex,
            String senderType,
            String content,
            String correctedText,
            String correctionExplanation,
            String grammarNote,
            String hintForLearner,
            Double pronunciationScore,
            File audioFile);

    Optional<ActiveSpeakingSessionResult> findActiveSession(Long userId, Long personaId);

    Optional<ActiveSpeakingSessionResult> findActiveSessionByCode(String sessionCode, Long userId);

    void updateSessionTurnAndTranscript(String sessionCode, int totalTurns, String fullTranscript);

    /**
     * Kiểm tra phiên đã hoàn thành và chấm điểm (status == COMPLETED) chưa.
     * Dùng để khóa không cho tiếp tục gửi tin nhắn vào phiên COMPLETED.
     */
    boolean isSessionCompleted(String sessionCode);

    /**
     * Đếm số phiên đang ở trạng thái IN_PROGRESS của người dùng.
     */
    int countActiveSessionsByUserId(Long userId);

    void deleteSessionBySessionCode(String sessionCode);

    boolean isSessionBelongToUser(String sessionCode, Long userId);
}
