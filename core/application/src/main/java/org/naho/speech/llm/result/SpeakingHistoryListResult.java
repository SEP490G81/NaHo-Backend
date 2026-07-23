package org.naho.speech.llm.result;

import java.util.List;

public record SpeakingHistoryListResult(
        List<SpeakingHistoryListItemResult> items,
        int page,
        int size,
        int totalPages,
        long totalElements
) {
}
