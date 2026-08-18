package org.naho.speech.llm.conversation.port.in;

import org.naho.speech.llm.conversation.command.SpeakingAnalysisCommand;
import org.naho.speech.llm.conversation.result.SpeakingAnalysisResult;

public interface SpeakingAnalysisInputPort {
    SpeakingAnalysisResult analyzeSpeaking(SpeakingAnalysisCommand command);
}
