package org.naho.speech.llm.question.repository;

import org.naho.speech.llm.question.entity.SpeakingAnalysisReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpeakingAnalysisReportJpaRepository extends JpaRepository<SpeakingAnalysisReportEntity, Long> {
    Optional<SpeakingAnalysisReportEntity> findByAnswerHistoryId(Long answerHistoryId);

    Optional<SpeakingAnalysisReportEntity> findBySpeechAssessmentId(Long speechAssessmentId);

    Optional<SpeakingAnalysisReportEntity> findByAiFeedbackId(Long aiFeedbackId);
}
