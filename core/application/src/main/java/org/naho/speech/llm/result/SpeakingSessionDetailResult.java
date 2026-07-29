package org.naho.speech.llm.result;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Chi tiết kết quả của một buổi nói chuyện AI 1-1 (bao gồm tất cả nhận xét từ AI).
 */
public record SpeakingSessionDetailResult(
        Long id,
        String sessionCode,
        String sessionType,
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

        // Assessment Scores & AI Feedback
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
