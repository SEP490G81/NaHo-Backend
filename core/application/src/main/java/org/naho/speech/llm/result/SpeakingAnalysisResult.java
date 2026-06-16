package org.naho.speech.llm.result;

import org.naho.social.model.Report;

import java.time.Instant;

public record SpeakingAnalysisResult (
       Long historyId,
       Double score
){
}
