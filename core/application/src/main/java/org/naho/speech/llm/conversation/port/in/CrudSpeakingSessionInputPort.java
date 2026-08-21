package org.naho.speech.llm.conversation.port.in;

import org.naho.speech.llm.conversation.result.SpeakingSessionListItemResult;
import org.naho.speech.llm.type.SpeakingSessionStatus;

import java.util.List;

public interface CrudSpeakingSessionInputPort {
    List<SpeakingSessionListItemResult> findAllByUserIdAndSpeakingSessionStatus(Long userId, SpeakingSessionStatus status);
}
