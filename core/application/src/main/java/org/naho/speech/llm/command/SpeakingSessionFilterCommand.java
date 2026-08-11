package org.naho.speech.llm.command;

import org.naho.question.constant.SpeakingHistorySortColumn;
import org.naho.shared.constant.SortDirection;

public record SpeakingSessionFilterCommand(
        Long userId,
        Long personaId,
        String search,
        String status,
        Integer page,
        Integer size,
        SpeakingHistorySortColumn sortColumn,
        SortDirection sortDirection
) {
}
