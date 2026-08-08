package org.naho.speech.llm.repository;

import org.naho.speech.llm.entity.SpeakingSessionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface SpeakingSessionJpaRepository extends JpaRepository<SpeakingSessionEntity, Long>, JpaSpecificationExecutor<SpeakingSessionEntity> {
    Optional<SpeakingSessionEntity> findBySessionCode(String sessionCode);

    Page<SpeakingSessionEntity> findByUserId(Long userId, Pageable pageable);

    Optional<SpeakingSessionEntity> findBySessionCodeAndUserId(String sessionCode, Long userId);

    Optional<SpeakingSessionEntity> findFirstByUserIdAndStatusOrderByStartedAtDesc(Long userId, String status);

    Optional<SpeakingSessionEntity> findFirstByUserIdAndPersonaIdAndStatusOrderByStartedAtDesc(Long userId, Long personaId, String status);

    @Modifying
    @Query("UPDATE SpeakingSessionEntity s SET s.status = :newStatus, s.endedAt = :endedAt WHERE s.status = :oldStatus AND s.startedAt < :cutoffTime")
    int updateExpiredSessions(
            @Param("oldStatus") String oldStatus,
            @Param("newStatus") String newStatus,
            @Param("cutoffTime") Instant cutoffTime,
            @Param("endedAt") Instant endedAt
    );
}

