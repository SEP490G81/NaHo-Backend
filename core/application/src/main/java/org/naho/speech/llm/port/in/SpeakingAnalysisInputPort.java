package org.naho.speech.llm.port.in;

import org.naho.speech.llm.command.SpeakingAnalysisCommand;
import org.naho.speech.llm.command.SpeakingHistoryFilterCommand;
import org.naho.speech.llm.result.SpeakingAnalysisResult;
import org.naho.speech.llm.result.SpeakingHistoryDetailResult;
import org.naho.speech.llm.result.SpeakingHistoryListResult;

public interface SpeakingAnalysisInputPort {
    SpeakingAnalysisResult analyzeSpeaking(SpeakingAnalysisCommand command);

    SpeakingHistoryDetailResult getHistoryDetail(Long historyId);

    SpeakingHistoryListResult getUserHistoryList(SpeakingHistoryFilterCommand command);
}
