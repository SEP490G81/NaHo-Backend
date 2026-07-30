package org.naho.speech.llm.repository;

import org.naho.speech.llm.entity.SpeakingSessionAssessmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpeakingSessionAssessmentJpaRepository extends JpaRepository<SpeakingSessionAssessmentEntity, Long> {
    Optional<SpeakingSessionAssessmentEntity> findBySessionId(Long sessionId);
}
