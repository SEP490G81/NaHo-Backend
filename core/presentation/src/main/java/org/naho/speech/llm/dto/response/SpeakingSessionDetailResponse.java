package org.naho.speech.llm.dto.response;

import org.naho.speech.llm.result.ScoringResult;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record SpeakingSessionDetailResponse(
        Long id,
        String sessionCode,
        String topic,
        Long personaId,
        String marugotoLevel,
        String formalityLevel,
        int totalTurns,
        int durationSeconds,
        Double asrConfidence,
        String fullTranscript,
        Instant startedAt,
        Instant endedAt,

        int overallScore,
        String jlptEstimate,
        int fluencyScore,
        int pronunciationScore,
        int grammarScore,
        int vocabularyScore,
        int interactionScore,
        int naturalnessScore,
        int coherenceScore,

        String summary,
        List<String> strengths,
        List<String> weaknesses,
        Map<String, String> feedback,

        List<ScoringResult.ImprovedExpression> improvedExpressions,
        ScoringResult.StudyRecommendation studyRecommendation
) {
}
