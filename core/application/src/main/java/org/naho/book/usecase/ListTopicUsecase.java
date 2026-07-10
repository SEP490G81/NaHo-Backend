package org.naho.book.usecase;

import org.naho.book.command.ListTopicCommand;
import org.naho.book.port.in.ListTopicInputPort;
import org.naho.book.port.out.TopicListRepositoryPort;
import org.naho.book.result.TopicListItemResult;
import org.naho.book.result.TopicListResult;
import org.naho.book.type.TopicStatus;

import java.util.List;

public class ListTopicUsecase implements ListTopicInputPort {

    private final TopicListRepositoryPort topicListRepositoryPort;

    public ListTopicUsecase(TopicListRepositoryPort topicListRepositoryPort) {
        this.topicListRepositoryPort = topicListRepositoryPort;
    }

    @Override
    public TopicListResult listTopics(ListTopicCommand query) {
        // Enforce ACTIVE status for non-admin users
        if (!query.isAdmin()) {
            query = query.withForcedStatus(TopicStatus.ACTIVE);
        }

        long totalElements = topicListRepositoryPort.countTopics(query);

        List<TopicListItemResult> items = List.of();
        int totalPages = 0;

        if (totalElements > 0) {
            items = topicListRepositoryPort.findTopics(query);
            totalPages = (int) Math.ceil((double) totalElements / query.size());
        }

        return new TopicListResult(
                items,
                query.page(),
                query.size(),
                totalPages,
                totalElements
        );
    }
}
