package org.naho.speech.llm.question.port.in;

import org.naho.question.result.AnswerHistoryResult;
import org.naho.speech.llm.question.command.SpeakingAnalysisCommand;

public interface SpeakingAnalysisInputPort {
    AnswerHistoryResult analyzeSpeaking(SpeakingAnalysisCommand command);
}
