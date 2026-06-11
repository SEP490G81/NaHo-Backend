package org.naho.speech.topic.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.speech.topic.command.ListTopicCommand;
import org.naho.speech.topic.mybatis.TopicQueryMapper;
import org.naho.speech.topic.port.out.TopicListRepositoryPort;
import org.naho.speech.topic.result.TopicListItemResult;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TopicListRepositoryAdapter implements TopicListRepositoryPort {

    private final TopicQueryMapper topicQueryMapper;

    @Override
    public long countTopics(ListTopicCommand query) {
        return topicQueryMapper.countTopics(query);
    }

    @Override
    public List<TopicListItemResult> findTopics(ListTopicCommand query) {
        int offset = (query.page() - 1) * query.size();
        return topicQueryMapper.findTopics(query, offset);
    }
}
