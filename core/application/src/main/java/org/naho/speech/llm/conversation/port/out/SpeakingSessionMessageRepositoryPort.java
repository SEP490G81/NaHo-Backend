package org.naho.speech.llm.conversation.port.out;

import org.naho.speech.llm.model.conversation.SpeakingSessionMessage;

import java.util.List;

public interface SpeakingSessionMessageRepositoryPort {
    List<SpeakingSessionMessage> findAllBySessionId(Long sessionId);
}
