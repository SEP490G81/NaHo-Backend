package org.naho.speech.llm.usecase;

import org.naho.speech.llm.port.in.EndSessionInputPort;
import org.naho.speech.llm.port.out.AiScoringPort;
import org.naho.speech.llm.port.out.SessionStorePort;
import org.naho.speech.llm.port.out.SpeakingSessionRepositoryPort;
import org.naho.speech.llm.result.ScoringResult;

import java.time.Instant;

public class EndSessionUseCase implements EndSessionInputPort {
    private final SessionStorePort sessionStorePort;
    private final AiScoringPort aiScoringPort;
    private final SpeakingSessionRepositoryPort speakingSessionRepositoryPort;

    public EndSessionUseCase(SessionStorePort sessionStorePort,
                             AiScoringPort aiScoringPort,
                             SpeakingSessionRepositoryPort speakingSessionRepositoryPort) {
        this.sessionStorePort = sessionStorePort;
        this.aiScoringPort = aiScoringPort;
        this.speakingSessionRepositoryPort = speakingSessionRepositoryPort;
    }

    @Override
    public ScoringResult endSession(String sessionId, String topic, String speechMetaData, String arsConfidence) {
        String fullTranscript = sessionStorePort.getFullTranscript(sessionId);
        String personaContext = sessionStorePort.getPersonaContext(sessionId);
        System.out.println("    Transcript length: " + fullTranscript.length() + " chars");
        System.out.println("    Persona context: " + (personaContext.isBlank() ? "(none)" : personaContext.substring(0, Math.min(80, personaContext.length()))));

        ScoringResult result = aiScoringPort.score(sessionId, topic, fullTranscript, speechMetaData, arsConfidence, personaContext);
        System.out.println("    overallScore: " + result.overallScore() + "/100");
        System.out.println("    jlptEstimate: " + result.jlptEstimate());

        // Persist session result to DB
        try {
            Long userId = sessionStorePort.getUserId(sessionId);
            Long personaId = sessionStorePort.getPersonaId(sessionId);
            String sessionType = sessionStorePort.getSessionType(sessionId);
            String marugotoLevel = sessionStorePort.getMarugotoLevel(sessionId);
            String formalityLevel = sessionStorePort.getFormalityLevel(sessionId);
            int totalTurns = sessionStorePort.getTurnCount(sessionId);
            Instant startedAt = sessionStorePort.getStartedAt(sessionId);

            Double asrConfidenceDouble = null;
            if (arsConfidence != null && !arsConfidence.isBlank() && !arsConfidence.equals("N/A")) {
                try {
                    asrConfidenceDouble = Double.parseDouble(arsConfidence.trim());
                } catch (NumberFormatException ignored) {}
            }

            speakingSessionRepositoryPort.saveSpeakingSession(
                    sessionId,
                    userId,
                    personaId,
                    topic,
                    sessionType,
                    marugotoLevel,
                    formalityLevel,
                    fullTranscript,
                    totalTurns,
                    asrConfidenceDouble,
                    startedAt,
                    result
            );
        } catch (Exception e) {
            // Non-critical: log and continue — don't fail the session end because of persistence error
            System.err.println("[EndSessionUseCase] Failed to persist session to DB: " + e.getMessage());
        }

        sessionStorePort.clearSession(sessionId);
        return result;
    }
}

