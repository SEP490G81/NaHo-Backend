package org.naho.speech.llm.command;

import org.naho.shared.constant.SortDirection;
import org.naho.speech.llm.constant.SpeakingHistorySortColumn;

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
