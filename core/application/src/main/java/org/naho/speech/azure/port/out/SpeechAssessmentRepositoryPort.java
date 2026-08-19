package org.naho.speech.azure.port.out;

import org.naho.speech.azure.model.SpeechAssessment;

import java.util.Optional;

public interface SpeechAssessmentRepositoryPort {
    SpeechAssessment createNew(SpeechAssessment speechAssessment);

    Optional<SpeechAssessment> findById(Long id);
}
