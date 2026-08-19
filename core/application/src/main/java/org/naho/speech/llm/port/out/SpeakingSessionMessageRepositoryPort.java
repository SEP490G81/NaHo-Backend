package org.naho.speech.llm.port.out;

import org.naho.speech.llm.model.SpeakingSessionMessage;

import java.util.List;

public interface SpeakingSessionMessageRepositoryPort {
    List<SpeakingSessionMessage> findAllBySessionId(Long sessionId);
}
