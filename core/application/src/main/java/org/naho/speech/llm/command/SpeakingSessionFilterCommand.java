package org.naho.speech.llm.command;

import org.naho.shared.constant.SortDirection;
import org.naho.question.constant.SpeakingHistorySortColumn;

public record SpeakingSessionFilterCommand(
        Long userId,
        Long personaId,
        String search,
        Integer page,
        Integer size,
        SpeakingHistorySortColumn sortColumn,
        SortDirection sortDirection
) {
}
