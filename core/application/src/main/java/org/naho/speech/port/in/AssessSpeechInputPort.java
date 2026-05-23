package org.naho.speech.port.in;

import org.naho.speech.command.SpeechAssessmentCommand;
import org.naho.speech.result.PronunciationAssessmentResult;

public interface AssessSpeechInputPort {
    PronunciationAssessmentResult execute(SpeechAssessmentCommand request);
}