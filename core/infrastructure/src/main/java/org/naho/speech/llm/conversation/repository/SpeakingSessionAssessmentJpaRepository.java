package org.naho.speech.llm.conversation.repository;

import org.naho.speech.llm.conversation.entity.SpeakingSessionAssessmentEntity;
import org.naho.speech.llm.type.SpeakingSessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpeakingSessionAssessmentJpaRepository extends JpaRepository<SpeakingSessionAssessmentEntity, Long> {
    Optional<SpeakingSessionAssessmentEntity> findBySpeakingSessionId(Long speakingSessionId);

    Optional<SpeakingSessionAssessmentEntity> findBySpeakingSession_SessionCodeAndSpeakingSession_StatusAndSpeakingSession_User_Id(
            String speakingSessionSessionCode,
            SpeakingSessionStatus speakingSessionStatus,
            Long speakingSessionUserId
    );
}
