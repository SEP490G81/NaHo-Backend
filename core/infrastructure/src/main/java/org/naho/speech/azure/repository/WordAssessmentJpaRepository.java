package org.naho.speech.azure.repository;

import org.naho.speech.azure.entity.WordAssessmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordAssessmentJpaRepository extends JpaRepository<WordAssessmentEntity, Long> {
}
