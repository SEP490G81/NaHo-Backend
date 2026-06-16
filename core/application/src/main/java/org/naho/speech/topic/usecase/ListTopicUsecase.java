package org.naho.speech.topic.usecase;

import org.naho.speech.topic.command.ListTopicCommand;
import org.naho.speech.topic.port.in.ListTopicInputPort;
import org.naho.speech.topic.port.out.TopicListRepositoryPort;
import org.naho.speech.topic.result.TopicListItemResult;
import org.naho.speech.topic.result.TopicListResult;
import org.naho.topic.type.TopicStatus;

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
