package org.naho.speech.llm.conversation.port.in;

import org.naho.speech.llm.conversation.result.SpeakingSessionListItemResult;
import org.naho.speech.llm.conversation.result.SpeakingSessionResult;
import org.naho.speech.llm.type.SpeakingSessionStatus;

import java.util.List;

public interface CrudSpeakingSessionInputPort {
    List<SpeakingSessionListItemResult> findAllByUserIdAndSpeakingSessionStatus(Long userId, SpeakingSessionStatus status);

    SpeakingSessionResult findByUserIdAndSpeakingSessionCodeAndSpeakingSessionStatus(
            Long userId,
            String sessionCode,
            SpeakingSessionStatus status
    );

    SpeakingSessionResult findBySessionCodeAndUserId(
            String sessionCode,
            Long userId
    );

    void deleteSessionBySessionCodeAndUserId(String sessionCode, Long userId);
}
