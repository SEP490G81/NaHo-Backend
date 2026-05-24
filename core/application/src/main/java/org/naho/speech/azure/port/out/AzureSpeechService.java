package org.naho.speech.azure.port.out;

import org.naho.speech.azure.command.SpeechAssessmentCommand;
import org.naho.speech.model.PronunciationAssessment;

public interface AzureSpeechService {
    PronunciationAssessment assess(SpeechAssessmentCommand command);
}