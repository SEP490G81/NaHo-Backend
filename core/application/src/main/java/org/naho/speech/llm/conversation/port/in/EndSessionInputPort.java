package org.naho.speech.llm.conversation.port.in;

import org.naho.speech.llm.conversation.result.ScoringResult;

public interface EndSessionInputPort {
    ScoringResult endSession(
            Long userId,
            String sessionCode,
            String topic,
            String speechMetaData,
            String arsConfidence
    );
}

