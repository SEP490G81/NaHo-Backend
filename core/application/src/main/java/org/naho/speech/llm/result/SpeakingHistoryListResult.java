package org.naho.speech.llm.result;

import java.util.List;

public record SpeakingHistoryListResult(
        List<SpeakingHistoryListItemResult> items,
        int currentPage,
        int pageSize,
        int totalPages,
        long totalElements
) {
}
