package org.naho.speech.llm.usecase;

import org.naho.shared.port.out.TransactionPort;
import org.naho.speech.llm.port.in.SpeakingSessionCleanupInputPort;
import org.naho.speech.llm.port.out.SessionStorePort;
import org.naho.speech.llm.port.out.SpeakingSessionRepositoryPort;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class SpeakingSessionCleanupUseCase implements SpeakingSessionCleanupInputPort {

    private static final int MEMORY_IDLE_EVICT_MINUTES = 45; // Speaking session ~15-25 phút

    private final SpeakingSessionRepositoryPort speakingSessionRepositoryPort;
    private final TransactionPort transactionPort;
    private final SessionStorePort sessionStorePort;

    public SpeakingSessionCleanupUseCase(
            SpeakingSessionRepositoryPort speakingSessionRepositoryPort,
            TransactionPort transactionPort,
            SessionStorePort sessionStorePort
    ) {
        this.speakingSessionRepositoryPort = speakingSessionRepositoryPort;
        this.transactionPort = transactionPort;
        this.sessionStorePort = sessionStorePort;
    }

    @Override
    public int cleanupExpiredSessions(int expireAfterHours) {
        // Chỉ evict các session idle khỏi RAM để tối ưu bộ nhớ máy chủ,
        // giữ nguyên các phiên IN_PROGRESS trong Database vĩnh viễn không tự động xóa.
        int evicted = sessionStorePort.evictIdleSessions(MEMORY_IDLE_EVICT_MINUTES);
        return evicted;
    }
}
