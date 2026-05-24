package org.naho.speech.azure.port.in;

import org.naho.speech.azure.command.SpeechAssessmentCommand;
import org.naho.speech.azure.result.PronunciationAssessmentResult;

public interface AssessSpeechInputPort {
    PronunciationAssessmentResult execute(SpeechAssessmentCommand request);
}