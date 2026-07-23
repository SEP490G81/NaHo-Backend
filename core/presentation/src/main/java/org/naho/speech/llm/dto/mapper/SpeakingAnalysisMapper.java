package org.naho.speech.llm.dto.mapper;

import org.naho.speech.llm.dto.response.SpeakingAnalysisResponse;
import org.naho.speech.llm.dto.response.SpeakingHistoryDetailResponse;
import org.naho.speech.llm.dto.response.SpeakingHistoryListItemResponse;
import org.naho.speech.llm.result.SpeakingAnalysisResult;
import org.naho.speech.llm.result.SpeakingHistoryDetailResult;
import org.naho.speech.llm.result.SpeakingHistoryListItemResult;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class SpeakingAnalysisMapper {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    public SpeakingAnalysisResponse toResponse(SpeakingAnalysisResult result) {
        if (result == null) return null;
        return new SpeakingAnalysisResponse(result.historyId(), result.score(), result.audioUrl());
    }

    public SpeakingHistoryListItemResponse toListItemResponse(SpeakingHistoryListItemResult result) {
        if (result == null) return null;
        return new SpeakingHistoryListItemResponse(
                result.historyId(),
                result.speakingQuestionId(),
                result.speakingQuestionTitle(),
                result.topicId(),
                result.topicName(),
                result.learningPathNodeId(),
                result.bookId(),
                result.score(),
                result.durationSec(),
                result.audioUrl(),
                result.practicedAt() != null ? ISO_FORMATTER.format(result.practicedAt()) : null
        );
    }

    public SpeakingHistoryDetailResponse toDetailResponse(SpeakingHistoryDetailResult result) {
        if (result == null) return null;

        var report = result.report();
        var scores = report.scores();

        var scoresResponse = new SpeakingHistoryDetailResponse.Scores(
                scores.pronunciation(),
                scores.vocabulary(),
                scores.grammar(),
                scores.naturalness()
        );

        var userTranscriptResponse = report.userTranscript().stream()
                .map(item -> new SpeakingHistoryDetailResponse.UserTranscriptItem(
                        item.text(),
                        item.error() != null ? new SpeakingHistoryDetailResponse.ErrorDetail(
                                item.error().type(),
                                item.error().explanation(),
                                item.error().suggestion()
                        ) : null
                )).toList();

        var aiSuggestionResponse = new SpeakingHistoryDetailResponse.AiSuggestion(
                report.aiSuggestion().jp(),
                report.aiSuggestion().furigana(),
                report.aiSuggestion().vi()
        );

        var pronunciationResponse = report.pronunciation().stream()
                .map(item -> new SpeakingHistoryDetailResponse.PronunciationItem(
                        item.text(),
                        item.furigana(),
                        item.severity(),
                        item.note()
                )).toList();

        var expressionsResponse = report.expressions().stream()
                .map(item -> new SpeakingHistoryDetailResponse.ExpressionItem(
                        item.jp(),
                        item.furigana(),
                        item.vi(),
                        item.note()
                )).toList();

        var itVocabResponse = report.itVocab().stream()
                .map(item -> new SpeakingHistoryDetailResponse.ItVocabItem(
                        item.term(),
                        item.reading(),
                        item.meaning()
                )).toList();

        var reportResponse = new SpeakingHistoryDetailResponse.Report(
                report.average(),
                scoresResponse,
                userTranscriptResponse,
                aiSuggestionResponse,
                pronunciationResponse,
                report.pronunciationNote(),
                expressionsResponse,
                itVocabResponse
        );

        return new SpeakingHistoryDetailResponse(
                result.historyId(),
                result.topicId(),
                result.questionId(),
                result.speakingQuestionTitle(),
                result.topicName(),
                result.learningPathNodeId(),
                result.bookId(),
                result.practicedAt() != null ? ISO_FORMATTER.format(result.practicedAt()) : null,
                result.durationSec(),
                result.score(),
                result.audioUrl(),
                reportResponse
        );
    }
}
