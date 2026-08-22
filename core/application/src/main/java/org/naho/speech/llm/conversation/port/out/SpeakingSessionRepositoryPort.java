package org.naho.speech.llm.conversation.port.out;

import org.naho.file.model.File;
import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;
import org.naho.speech.llm.conversation.result.SpeakingSessionAssessmentResult;
import org.naho.speech.llm.model.conversation.SpeakingSession;
import org.naho.speech.llm.type.MessageType;
import org.naho.speech.llm.type.SpeakingSessionStatus;

import java.time.Instant;
import java.util.List;

public interface SpeakingSessionRepositoryPort {

    SpeakingSession saveSpeakingSession(
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
            SpeakingSessionAssessmentResult speakingSessionAssessmentResult
    );

    SpeakingSession initSpeakingSession(
            String sessionCode,
            Long userId,
            Long personaId,
            String topic,
            String voiceName,
            FormalityLevel formalityLevel,
            MarugotoLevel marugotoLevel
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

    void updateSessionTurnAndTranscript(
            String sessionCode,
            int totalTurns,
            String fullTranscript
    );

    void updateSessionTurnAndTranscriptAndStatus(
            String sessionCode,
            int totalTurns,
            String fullTranscript,
            SpeakingSessionStatus status
    );

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

    SpeakingSession findBySessionCode(String sessionCode);

    SpeakingSession findBySessionCodeAndStatus(String sessionCode, SpeakingSessionStatus status);

    SpeakingSession findBySessionId(Long sessionId);

    List<SpeakingSession> findAllByUserIdAndSpeakingSessionStatus(Long userId, SpeakingSessionStatus status);
}
