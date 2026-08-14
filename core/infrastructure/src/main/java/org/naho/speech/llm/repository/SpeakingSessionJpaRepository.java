package org.naho.speech.llm.repository;

import org.naho.speech.llm.entity.SpeakingSessionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface SpeakingSessionJpaRepository
        extends JpaRepository<SpeakingSessionEntity, Long>, JpaSpecificationExecutor<SpeakingSessionEntity> {
    Optional<SpeakingSessionEntity> findBySessionCode(String sessionCode);

    Page<SpeakingSessionEntity> findByUserId(Long userId, Pageable pageable);

    Optional<SpeakingSessionEntity> findBySessionCodeAndUserId(String sessionCode, Long userId);

    Optional<SpeakingSessionEntity> findBySessionCodeAndStatus(String sessionCode, String status);

    Optional<SpeakingSessionEntity> findFirstByUserIdAndStatusOrderByStartedAtDesc(Long userId, String status);

    Optional<SpeakingSessionEntity> findFirstByUserIdAndPersonaIdAndStatusOrderByStartedAtDesc(Long userId,
            Long personaId, String status);

    int countByUserIdAndStatus(Long userId, String status);
}
