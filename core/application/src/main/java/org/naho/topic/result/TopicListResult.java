package org.naho.topic.result;

import java.util.List;

public record TopicListResult(
        List<TopicListItemResult> items,
        int currentPage,
        int pageSize,
        int totalPages,
        long totalElements
) {
}
