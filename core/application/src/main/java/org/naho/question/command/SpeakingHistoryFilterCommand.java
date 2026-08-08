package org.naho.question.command;

import org.naho.question.constant.SpeakingHistorySortColumn;
import org.naho.shared.constant.SortDirection;

public record SpeakingHistoryFilterCommand(
        Long userId,
        Long speakingQuestionId,
        Long topicId,
        String search,
        Integer page,
        Integer size,
        SpeakingHistorySortColumn sortColumn,
        SortDirection sortDirection
) {
}
