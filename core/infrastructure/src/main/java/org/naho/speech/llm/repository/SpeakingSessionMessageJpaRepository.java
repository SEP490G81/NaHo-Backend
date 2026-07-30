package org.naho.speech.llm.repository;

import org.naho.speech.llm.entity.SpeakingSessionMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpeakingSessionMessageJpaRepository extends JpaRepository<SpeakingSessionMessageEntity, Long> {
    List<SpeakingSessionMessageEntity> findBySessionIdOrderByTurnIndex(Long sessionId);
}
