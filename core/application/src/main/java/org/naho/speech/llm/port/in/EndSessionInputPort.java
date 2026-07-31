package org.naho.speech.llm.port.in;

import org.naho.speech.llm.result.ScoringResult;

public interface EndSessionInputPort {
    ScoringResult endSession(
            Long userId,
            String sessionId,
            String topic,
            String speechMetaData,
            String arsConfidence
    );
}

