package org.naho.speech.azure.repository;

import org.naho.shared.persistence.BaseJpaRepository;
import org.naho.speech.azure.entity.WordAssessmentEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface WordAssessmentJpaRepository extends BaseJpaRepository<WordAssessmentEntity> {
}
