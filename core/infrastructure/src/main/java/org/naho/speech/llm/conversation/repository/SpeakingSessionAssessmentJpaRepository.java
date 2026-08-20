package org.naho.speech.llm.conversation.repository;

import org.naho.speech.llm.conversation.entity.SpeakingSessionAssessmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpeakingSessionAssessmentJpaRepository extends JpaRepository<SpeakingSessionAssessmentEntity, Long> {
    Optional<SpeakingSessionAssessmentEntity> findBySpeakingSessionId(Long speakingSessionId);
}
