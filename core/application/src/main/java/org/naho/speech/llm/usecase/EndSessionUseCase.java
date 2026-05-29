package org.naho.speech.llm.usecase;

import org.naho.speech.llm.port.in.EndSessionInputPort;
import org.naho.speech.llm.port.out.AiScoringPort;
import org.naho.speech.llm.port.out.SessionStorePort;
import org.naho.speech.llm.result.ScoringResult;

public class EndSessionUseCase implements EndSessionInputPort {
    private final SessionStorePort sessionStorePort;
    private final AiScoringPort aiScoringPort;

    public EndSessionUseCase(SessionStorePort sessionStorePort, AiScoringPort aiScoringPort) {
        this.sessionStorePort = sessionStorePort;
        this.aiScoringPort = aiScoringPort;
    }

    @Override
    public ScoringResult endSession(String sessionId, String topic, String speechMetaData, String arsConfidence) {
        String fullTranscript = sessionStorePort.getFullTranscript(sessionId);
        System.out.println("    Transcript length: " + fullTranscript.length() + " chars");
        ScoringResult result = aiScoringPort.score(sessionId, topic, fullTranscript, speechMetaData, arsConfidence);
        System.out.println("    overallScore: " + result.overallScore() + "/100");
        System.out.println("    jlptEstimate: " + result.jlptEstimate());

        sessionStorePort.clearSession(sessionId);
        return result;
    }
}
