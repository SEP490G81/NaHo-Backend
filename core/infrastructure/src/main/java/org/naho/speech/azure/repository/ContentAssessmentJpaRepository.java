package org.naho.speech.azure.repository;

import org.naho.speech.azure.entity.ContentAssessmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContentAssessmentJpaRepository extends JpaRepository<ContentAssessmentEntity, Long> {
    Optional<ContentAssessmentEntity> findByAnswerHistoryId(Long answerHistoryId);
}
