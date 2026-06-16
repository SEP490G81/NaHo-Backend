package org.naho.speech.llm.dto.response;

import java.util.List;

public record SpeakingHistoryDetailResponse(
        String historyId,
        String topicId,
        String questionId,
        String practicedAt,
        Integer durationSec,
        Double score,
        String audioUrl,
        Report report
) {
    public record Report(
            Double average,
            Scores scores,
            List<UserTranscriptItem> userTranscript,
            AiSuggestion aiSuggestion,
            List<PronunciationItem> pronunciation,
            String pronunciationNote,
            List<ExpressionItem> expressions,
            List<ItVocabItem> itVocab
    ) {}

    public record Scores(
            Double pronunciation,
            Double vocabulary,
            Double grammar,
            Double naturalness
    ) {}

    public record UserTranscriptItem(
            String text,
            ErrorDetail error
    ) {}

    public record ErrorDetail(
            String type,
            String explanation,
            String suggestion
    ) {}

    public record AiSuggestion(
            String jp,
            String furigana,
            String vi
    ) {}

    public record PronunciationItem(
            String text,
            String furigana,
            String severity,
            String note
    ) {}

    public record ExpressionItem(
            String jp,
            String furigana,
            String vi,
            String note
    ) {}

    public record ItVocabItem(
            String term,
            String reading,
            String meaning
    ) {}
}
