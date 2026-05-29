package org.naho.speech.azure.port.out;

import org.naho.speech.azure.command.SpeechAssessmentCommand;
import org.naho.speech.model.SpeechAssessment;

public interface AzureSpeechServicePort {
    SpeechAssessment assess(SpeechAssessmentCommand command);
}