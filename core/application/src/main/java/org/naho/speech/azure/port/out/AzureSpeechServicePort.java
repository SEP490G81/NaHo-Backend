package org.naho.speech.azure.port.out;

import org.naho.speech.azure.model.SpeechAssessment;
import org.naho.speech.azure.command.SpeechAssessmentCommand;

public interface AzureSpeechServicePort {
    SpeechAssessment assess(SpeechAssessmentCommand command);
}