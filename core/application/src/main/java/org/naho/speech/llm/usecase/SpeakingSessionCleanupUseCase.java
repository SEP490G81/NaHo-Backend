package org.naho.speech.llm.usecase;

import org.naho.shared.port.out.TransactionPort;
import org.naho.speech.llm.port.in.SpeakingSessionCleanupInputPort;
import org.naho.speech.llm.port.out.SpeakingSessionRepositoryPort;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class SpeakingSessionCleanupUseCase implements SpeakingSessionCleanupInputPort {

    private final SpeakingSessionRepositoryPort speakingSessionRepositoryPort;
    private final TransactionPort transactionPort;

    public SpeakingSessionCleanupUseCase(
            SpeakingSessionRepositoryPort speakingSessionRepositoryPort,
            TransactionPort transactionPort
    ) {
        this.speakingSessionRepositoryPort = speakingSessionRepositoryPort;
        this.transactionPort = transactionPort;
    }

    @Override
    public int cleanupExpiredSessions(int expireAfterHours) {
        Instant cutoffTime = Instant.now().minus(expireAfterHours, ChronoUnit.HOURS);
        Instant now = Instant.now();

        return transactionPort.execute(() ->
                speakingSessionRepositoryPort.updateStatusForExpiredSessions(
                        "IN_PROGRESS",
                        "EXPIRED",
                        cutoffTime,
                        now
                )
        );
    }
}
