package org.naho.speech.llm.conversation.repository;

import org.naho.speech.llm.conversation.entity.SpeakingSessionEntity;
import org.naho.speech.llm.type.SpeakingSessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface SpeakingSessionJpaRepository
        extends JpaRepository<SpeakingSessionEntity, Long>, JpaSpecificationExecutor<SpeakingSessionEntity> {
    Optional<SpeakingSessionEntity> findBySessionCode(String sessionCode);

    Optional<SpeakingSessionEntity> findBySessionCodeAndStatus(String sessionCode, SpeakingSessionStatus status);

    int countByUserIdAndStatus(Long userId, SpeakingSessionStatus status);

    List<SpeakingSessionEntity> findAllByUserIdAndStatus(Long userId, SpeakingSessionStatus status);

    Optional<SpeakingSessionEntity> findBySessionCodeAndUser_Id(String sessionCode, Long userId);

    Optional<SpeakingSessionEntity> findByUser_IdAndSessionCodeAndStatus(Long userId, String sessionCode, SpeakingSessionStatus status);
}
