package org.naho.speech.llm.usecase;

import org.naho.i18n.message.llm.LlmDetailMessageKey;
import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.llm.exception.LlmApplicationError;
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

    public EndSessionUseCase(
            SessionStorePort sessionStorePort,
            AiScoringPort aiScoringPort,
            SpeakingSessionRepositoryPort speakingSessionRepositoryPort
    ) {
        this.sessionStorePort = sessionStorePort;
        this.aiScoringPort = aiScoringPort;
        this.speakingSessionRepositoryPort = speakingSessionRepositoryPort;
    }

    @Override
    public ScoringResult endSession(Long requestUserId, String sessionCode, String topic, String speechMetaData, String arsConfidence) {
        // Lấy user id của người sở hữu cái session này
        Long userId = sessionStorePort.getUserId(sessionCode);
        if (userId == null) {
            userId = requestUserId;
        }

        if (userId == null) {
            throw new ApplicationException(
                    LlmApplicationError.LLM_SESSION_NOT_FOUND,
                    LlmDetailMessageKey.LLM_SESSION_NOT_FOUND
            );
        }

        String fullTranscript = sessionStorePort.getFullTranscript(sessionCode);
        String personaContext = sessionStorePort.getPersonaContext(sessionCode);

        String effectiveTopic = (topic != null && !topic.isBlank()) ? topic : sessionStorePort.getTopic(sessionCode);

        System.out.println("    Transcript length: " + fullTranscript.length() + " chars");
        System.out.println("    Persona context: " + (personaContext.isBlank() ? "(none)" : personaContext.substring(0, Math.min(80, personaContext.length()))));

        ScoringResult result = aiScoringPort.score(sessionCode, effectiveTopic, fullTranscript, speechMetaData, arsConfidence, personaContext);
        System.out.println("    overallScore: " + result.overallScore() + "/100");
        System.out.println("    jlptEstimate: " + result.jlptEstimate());

        // Persist session result to DB
        try {
            Long personaId = sessionStorePort.getPersonaId(sessionCode);
            MarugotoLevel marugotoLevel = sessionStorePort.getMarugotoLevel(sessionCode);
            FormalityLevel formalityLevel = sessionStorePort.getFormalityLevel(sessionCode);
            int totalTurns = sessionStorePort.getTurnCount(sessionCode);
            Instant startedAt = sessionStorePort.getStartedAt(sessionCode);

            Double asrConfidenceDouble = null;
            if (arsConfidence != null && !arsConfidence.isBlank() && !arsConfidence.equals("N/A")) {
                try {
                    asrConfidenceDouble = Double.parseDouble(arsConfidence.trim());
                } catch (NumberFormatException ignored) {
                    throw new ApplicationException(
                            LlmApplicationError.LLM_PARSE_ERROR,
                            LlmDetailMessageKey.LLM_PARSE_ERROR
                    );
                }
            }

            speakingSessionRepositoryPort.saveSpeakingSession(
                    sessionCode,
                    userId,
                    personaId,
                    effectiveTopic,
                    marugotoLevel,
                    formalityLevel,
                    fullTranscript,
                    totalTurns,
                    asrConfidenceDouble,
                    startedAt,
                    result
            );
            System.out.println("[EndSessionUseCase] Successfully saved session " + sessionCode + " to DB for userId: " + userId);
        } catch (Exception e) {
            System.err.println("[EndSessionUseCase] Failed to persist session to DB: " + e.getMessage());
            throw new ApplicationException(
                    LlmApplicationError.LLM_SAVE_SESSION_FAILED,
                    LlmDetailMessageKey.LLM_SAVE_SESSION_FAILED
            );
        }

        sessionStorePort.clearSession(sessionCode);
        return result;
    }
}

