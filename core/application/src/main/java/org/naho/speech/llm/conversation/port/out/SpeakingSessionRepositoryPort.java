package org.naho.speech.llm.conversation.port.out;

import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;
import org.naho.speech.llm.conversation.command.SpeakingSessionMessageCommand;
import org.naho.speech.llm.model.conversation.SpeakingSession;
import org.naho.speech.llm.model.conversation.SpeakingSessionMessage;
import org.naho.speech.llm.type.SpeakingSessionStatus;

import java.util.List;

public interface SpeakingSessionRepositoryPort {
    SpeakingSession save(SpeakingSession speakingSession);

    SpeakingSession increaseTotalTurns(Long sessionId);

    SpeakingSession initSpeakingSession(
            String sessionCode,
            Long userId,
            Long personaId,
            String topic,
            String voiceName,
            FormalityLevel formalityLevel,
            MarugotoLevel marugotoLevel
    );

    SpeakingSessionMessage saveSpeakingSessionMessage(
            SpeakingSessionMessageCommand command
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

    SpeakingSession findBySessionId(Long sessionId);

    List<SpeakingSession> findAllByUserIdAndSpeakingSessionStatus(Long userId, SpeakingSessionStatus status);

    SpeakingSession findByUserIdAndSpeakingSessionCodeAndSpeakingSessionStatus(Long userId, String sessionCode, SpeakingSessionStatus status);
}
