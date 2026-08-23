package org.naho.speech.llm.conversation.port.out;

import org.naho.speech.llm.model.conversation.SpeakingSessionAssessment;
import org.naho.speech.llm.type.SpeakingSessionStatus;

import java.util.Optional;

public interface SpeakingSessionAssessmentRepositoryPort {
    SpeakingSessionAssessment save(SpeakingSessionAssessment speakingSessionAssessment);

    SpeakingSessionAssessment findById(Long id);

    Optional<SpeakingSessionAssessment> findBySpeakingSession_SessionCodeAndSpeakingSession_StatusAndSpeakingSession_User_Id(
            String speakingSessionSessionCode,
            SpeakingSessionStatus speakingSessionStatus,
            Long speakingSessionUserId
    );
}
