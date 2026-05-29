package org.naho.speech.llm.port.in;

import org.naho.speech.llm.result.ScoringResult;

public interface EndSessionInputPort {
    ScoringResult endSession(
            String sessionId,
            String topic,
            String speechMetaData,
            String arsConfidence
    );
}

