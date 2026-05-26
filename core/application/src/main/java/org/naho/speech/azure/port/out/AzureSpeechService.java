package org.naho.speech.azure.port.out;

import org.naho.speech.azure.command.SpeechAssessmentCommand;
import org.naho.speech.model.SpeechAssessment;

public interface AzureSpeechService {
    SpeechAssessment assess(SpeechAssessmentCommand command);
}