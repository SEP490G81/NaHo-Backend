package org.naho.speech.llm.port.out;

import org.naho.file.model.File;
import org.naho.pagination.PageData;
import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;
import org.naho.speech.llm.command.SpeakingSessionFilterCommand;
import org.naho.speech.llm.model.SpeakingSession;
import org.naho.speech.llm.result.ScoringResult;
import org.naho.speech.llm.result.SpeakingSessionDetailResult;
import org.naho.speech.llm.result.SpeakingSessionListItemResult;
import org.naho.speech.llm.type.MessageType;
import org.naho.speech.llm.type.SpeakingSessionStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface SpeakingSessionRepositoryPort {

    void saveSpeakingSession(
            String sessionCode,
            Long userId,
            Long personaId,
            String topic,
            MarugotoLevel marugotoLevel,
            FormalityLevel formalityLevel,
            String fullTranscript,
            int totalTurns,
            Double asrConfidence,
            Instant startedAt,
            ScoringResult scoringResult);

    PageData<SpeakingSessionListItemResult> findUserSessions(SpeakingSessionFilterCommand command);

    Optional<SpeakingSessionDetailResult> findSessionDetailByCode(String sessionCode, Long userId);

    SpeakingSession createInProgressSession(
            String sessionCode,
            Long userId,
            Long personaId,
            String topic,
            String voiceName,
            MarugotoLevel marugotoLevel,
            FormalityLevel formalityLevel
    );

    void saveSessionMessage(
            String sessionCode,
            int turnIndex,
            String senderType,
            MessageType messageType,
            String content,
            String contentTranslation,
            String correctedText,
            String correctionExplanation,
            String grammarNote,
            String hintForLearner,
            Double pronunciationScore
    );

    void saveSessionMessage(
            String sessionCode,
            int turnIndex,
            String senderType,
            MessageType messageType,
            String content,
            String contentTranslation,
            String correctedText,
            String correctionExplanation,
            String grammarNote,
            String hintForLearner,
            Double pronunciationScore,
            File audioFile
    );

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

    boolean isSessionStarted(String sessionCode);

    SpeakingSession findBySessionCode(String sessionCode);

    SpeakingSession findBySessionCodeAndStatus(String sessionCode, SpeakingSessionStatus status);

    SpeakingSession findBySessionId(Long sessionId);

    List<SpeakingSession> findAllInProgressSessionsByUserId(Long userId);
}
