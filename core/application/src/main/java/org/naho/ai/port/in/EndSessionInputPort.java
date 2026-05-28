package org.naho.ai.port.in;

import org.naho.ai.result.ScoringResult;

public interface EndSessionInputPort {
    ScoringResult endSession(
            String sessionId,
            String topic,
            String speechMetaData,
            String arsConfidence
    );
}

