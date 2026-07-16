package org.naho.book.result;

import java.util.List;

public record TopicListResult(
        List<TopicResult> items,
        int currentPage,
        int pageSize,
        int totalPages,
        long totalElements
) {
}
