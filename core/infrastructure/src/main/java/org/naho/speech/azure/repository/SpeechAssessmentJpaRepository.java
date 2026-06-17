package org.naho.speech.azure.repository;

import org.naho.speech.azure.entity.SpeechAssessmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpeechAssessmentJpaRepository extends JpaRepository<SpeechAssessmentEntity, Long> {
    Optional<SpeechAssessmentEntity> findByAnswerHistoryId(Long answerHistoryId);
}
