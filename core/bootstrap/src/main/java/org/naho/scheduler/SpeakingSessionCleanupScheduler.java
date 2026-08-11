package org.naho.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.speech.llm.port.in.SpeakingSessionCleanupInputPort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SpeakingSessionCleanupScheduler {

    private final SpeakingSessionCleanupInputPort speakingSessionCleanupInputPort;

    /**
     * Chạy định kỳ 30 phút một lần (1.800.000 ms)
     * Dọn dẹp các phiên nói chuyện IN_PROGRESS đã khởi tạo quá 2 giờ.
     * Đồng thời evict session idle > 45 phút khỏi memory.
     */
    @Scheduled(fixedDelay = 1_800_000L)
    public void cleanupExpiredSpeakingSessions() {
        int count = speakingSessionCleanupInputPort.cleanupExpiredSessions(2);
        log.info("[SpeakingSession] Cleanup complete — {} DB sessions marked EXPIRED, idle memory sessions evicted", count);
    }
}

