package org.naho.speech.llm.conversation.port.in;

import org.naho.speech.llm.conversation.result.SpeakingSessionListItemResult;

import java.util.List;

public interface CrudSpeakingSessionInputPort {
    List<SpeakingSessionListItemResult> findAllInProgressSessionsByUserId(Long userId);
}
