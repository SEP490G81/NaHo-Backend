package org.naho.question.result;

import java.util.List;

public record SpeakingHistoryDetailResult(
        Long historyId,
        Long topicId,
        Long questionId,
        String speakingQuestionTitle,
        String topicName,
        Long learningPathNodeId,
        Long bookId,
        Double duration,
        Double score,
        Report report,
        String audioUrl
) {
    public record Report(
            Double average,
            Scores scores,
            String fullTranscript,
            List<UserTranscriptItem> userTranscript,
            AiSuggestion aiSuggestion,
            List<PronunciationItem> pronunciation,
            String pronunciationNote,
            List<ExpressionItem> expressions,
            List<ItVocabItem> itVocab
    ) {
    }

    public record Scores(
            Double pronunciation,
            Double vocabulary,
            Double grammar,
            Double naturalness
    ) {
    }

    public record UserTranscriptItem(
            String text,
            ErrorDetail error
    ) {
    }

    public record ErrorDetail(
            String type,
            String explanation,
            String suggestion
    ) {
    }

    public record AiSuggestion(
            String jp,
            String furigana,
            String vi
    ) {
    }

    public record PronunciationItem(
            String text,
            String furigana,
            String severity,
            String note,
            Double accuracyScore
    ) {
    }

    public record ExpressionItem(
            String jp,
            String furigana,
            String vi,
            String note
    ) {
    }

    public record ItVocabItem(
            String term,
            String reading,
            String meaning
    ) {
    }
}
