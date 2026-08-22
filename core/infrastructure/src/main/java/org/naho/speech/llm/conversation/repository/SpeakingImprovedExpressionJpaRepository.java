package org.naho.speech.llm.conversation.repository;

import org.naho.speech.llm.conversation.entity.SpeakingImprovedExpressionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpeakingImprovedExpressionJpaRepository extends JpaRepository<SpeakingImprovedExpressionEntity, Long> {
    List<SpeakingImprovedExpressionEntity> findBySpeakingSessionAssessmentId(Long speakingSessionAssessmentId);
}
