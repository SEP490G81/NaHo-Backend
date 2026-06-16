package org.naho.speech.llm.port.in;

import org.naho.speech.llm.result.SpeakingHistoryDetailResult;
import org.naho.speech.llm.command.SpeakingAnalysisCommand;
import org.naho.speech.llm.result.SpeakingAnalysisResult;

public interface SpeakingAnalysisInputPort {
    SpeakingAnalysisResult analyzeSpeaking(SpeakingAnalysisCommand command);
    SpeakingHistoryDetailResult getHistoryDetail(Long historyId);
}
