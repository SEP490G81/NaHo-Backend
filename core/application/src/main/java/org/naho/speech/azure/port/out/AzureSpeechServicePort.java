package org.naho.speech.azure.port.out;

import org.naho.speech.azure.command.SpeechAssessmentCommand;
import org.naho.speech.azure.model.SpeechAssessment;

public interface AzureSpeechServicePort {
    SpeechAssessment assessAudio(SpeechAssessmentCommand command);
}