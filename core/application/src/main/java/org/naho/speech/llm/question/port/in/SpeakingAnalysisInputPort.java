package org.naho.speech.llm.question.port.in;

import org.naho.speech.llm.question.command.SpeakingAnalysisCommand;
import org.naho.speech.llm.question.result.SpeakingAnalysisResult;

public interface SpeakingAnalysisInputPort {
    SpeakingAnalysisResult analyzeSpeaking(SpeakingAnalysisCommand command);
}
