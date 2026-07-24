package org.naho.speech.llm.dto.response;

import java.util.List;

public record SpeakingHistoryListResponse(
        List<SpeakingHistoryListItemResponse> items,
        int currentPage,
        int pageSize,
        int totalPages,
        long totalElements
) {
}
