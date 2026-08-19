package org.naho.speech.azure.repository;

import org.naho.shared.persistence.BaseJpaRepository;
import org.naho.speech.azure.entity.SpeechAssessmentEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface SpeechAssessmentJpaRepository extends BaseJpaRepository<SpeechAssessmentEntity> {
}
