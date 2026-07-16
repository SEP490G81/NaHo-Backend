package org.naho.book.result;

import org.naho.book.type.TopicStatus;
import org.naho.question.result.SpeakingQuestionListItemResult;

import java.util.List;

public record ObjectiveDetailResult(
        Long id,
        String japaneseName,
        String japaneseDescription,
        String japaneseNameMarkup,
        String japaneseDescriptionMarkup,
        TopicStatus status,
        Double orderIndex,
        List<SpeakingQuestionListItemResult> questions
) {
}
