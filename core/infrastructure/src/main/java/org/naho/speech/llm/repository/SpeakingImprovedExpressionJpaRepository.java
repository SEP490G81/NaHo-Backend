package org.naho.speech.llm.repository;

import org.naho.speech.llm.entity.SpeakingImprovedExpressionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpeakingImprovedExpressionJpaRepository extends JpaRepository<SpeakingImprovedExpressionEntity, Long> {
    List<SpeakingImprovedExpressionEntity> findByAssessmentId(Long assessmentId);
}
