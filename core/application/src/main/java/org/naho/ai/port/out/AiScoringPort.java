package org.naho.ai.port.out;

import org.naho.ai.result.ScoringResult;

public interface AiScoringPort {
    ScoringResult score(
            String sessionId,
            String topic,
            String fullTranscript,
            String speechMetadata,
            String asrConfidence
    );
}
