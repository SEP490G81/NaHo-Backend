package org.naho.speech.llm.port.out;

import org.naho.speech.llm.result.ScoringResult;

public interface AiScoringPort {
    ScoringResult score(
            String sessionCode,
            String topic,
            String fullTranscript,
            String speechMetadata,
            String asrConfidence,
            String personaContext
    );
}
