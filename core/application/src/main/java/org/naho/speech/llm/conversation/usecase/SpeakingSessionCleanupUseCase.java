package org.naho.speech.llm.conversation.usecase;

import org.naho.speech.llm.conversation.port.in.SpeakingSessionCleanupInputPort;
import org.naho.speech.llm.conversation.port.out.SessionStorePort;
import org.naho.speech.llm.conversation.port.out.SpeakingSessionRepositoryPort;
import org.naho.speech.llm.conversation.validator.SessionValidator;

public class SpeakingSessionCleanupUseCase implements SpeakingSessionCleanupInputPort {
    private final SessionStorePort sessionStorePort;
    private final SpeakingSessionRepositoryPort speakingSessionRepositoryPort;
    private final SessionValidator sessionValidator;

    public SpeakingSessionCleanupUseCase(
            SessionStorePort sessionStorePort,
            SpeakingSessionRepositoryPort speakingSessionRepositoryPort,
            SessionValidator sessionValidator
    ) {
        this.sessionStorePort = sessionStorePort;
        this.speakingSessionRepositoryPort = speakingSessionRepositoryPort;
        this.sessionValidator = sessionValidator;
    }

    @Override
    public void deleteSession(String sessionCode, Long userId) {
        sessionValidator.validateSessionIsBelongToUser(sessionCode, userId);
        sessionStorePort.clearSession(sessionCode);
        speakingSessionRepositoryPort.deleteSessionBySessionCode(sessionCode);
    }
}
