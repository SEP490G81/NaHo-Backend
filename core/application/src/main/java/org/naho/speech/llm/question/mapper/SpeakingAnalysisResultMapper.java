package org.naho.speech.llm.question.mapper;

import org.naho.speech.llm.model.question.SpeakingAnalysis;
import org.naho.speech.llm.question.result.SpeakingAnalysisResult;

public class SpeakingAnalysisResultMapper {
    SpeakingAnalysisResult domainToResult(SpeakingAnalysis domain) {
        if (domain == null)
            return null;
        return SpeakingAnalysisResult.builder().build();
    }
}
