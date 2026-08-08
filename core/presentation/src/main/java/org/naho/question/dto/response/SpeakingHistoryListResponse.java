package org.naho.question.dto.response;

import java.util.List;

public record SpeakingHistoryListResponse(
        List<SpeakingHistoryListItemResponse> items,
        int currentPage,
        int pageSize,
        int totalPages,
        long totalElements
) {
}
