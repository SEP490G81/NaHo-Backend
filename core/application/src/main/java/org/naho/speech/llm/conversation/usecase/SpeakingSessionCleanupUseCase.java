package org.naho.speech.llm.conversation.usecase;

import org.naho.speech.llm.conversation.port.in.SpeakingSessionCleanupInputPort;
import org.naho.speech.llm.conversation.port.out.SpeakingSessionRepositoryPort;
import org.naho.speech.llm.conversation.validator.SpeakingSessionValidator;

public class SpeakingSessionCleanupUseCase implements SpeakingSessionCleanupInputPort {
    private final SpeakingSessionRepositoryPort speakingSessionRepositoryPort;
    private final SpeakingSessionValidator speakingSessionValidator;

    public SpeakingSessionCleanupUseCase(
            SpeakingSessionRepositoryPort speakingSessionRepositoryPort,
            SpeakingSessionValidator speakingSessionValidator
    ) {
        this.speakingSessionRepositoryPort = speakingSessionRepositoryPort;
        this.speakingSessionValidator = speakingSessionValidator;
    }

    @Override
    public void deleteSession(String sessionCode, Long userId) {
        speakingSessionValidator.validateSessionIsBelongToUser(sessionCode, userId);
        speakingSessionRepositoryPort.deleteSessionBySessionCode(sessionCode);
    }
}
