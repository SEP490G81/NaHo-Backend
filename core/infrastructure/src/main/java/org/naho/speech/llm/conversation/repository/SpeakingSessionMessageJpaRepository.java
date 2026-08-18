package org.naho.speech.llm.conversation.repository;

import org.naho.speech.llm.conversation.entity.SpeakingSessionMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpeakingSessionMessageJpaRepository extends JpaRepository<SpeakingSessionMessageEntity, Long> {
    List<SpeakingSessionMessageEntity> findBySessionIdOrderByTurnIndexAscIdAsc(Long sessionId);
}
