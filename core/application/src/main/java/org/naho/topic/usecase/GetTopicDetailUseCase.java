package org.naho.topic.usecase;

import org.naho.i18n.message.topic.TopicDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.topic.command.GetTopicDetailCommand;
import org.naho.topic.exception.TopicErrorCode;
import org.naho.topic.model.Topic;
import org.naho.topic.port.in.GetTopicDetailInputPort;
import org.naho.topic.port.out.TopicRepositoryPort;
import org.naho.topic.result.TopicDetailResult;

public class GetTopicDetailUseCase implements GetTopicDetailInputPort {

    private final TopicRepositoryPort topicRepositoryPort;

    public GetTopicDetailUseCase(TopicRepositoryPort topicRepositoryPort) {
        this.topicRepositoryPort = topicRepositoryPort;
    }

    @Override
    public TopicDetailResult getTopicDetail(GetTopicDetailCommand command) {
        Topic topic = topicRepositoryPort.findById(command.id())
                .orElseThrow(() -> new ApplicationException(
                        TopicErrorCode.TOPIC_NOT_FOUND,
                        TopicDetailMessageKey.TOPIC_ID_NOT_FOUND,
                        command.id()));

        return new TopicDetailResult(
                topic.getId(),
                topic.getUserId(),
                topic.getJapaneseName(),
                topic.getJapaneseDescription(),
                topic.getJapaneseNameMarkup(),
                topic.getJapaneseDescriptionMarkup(),
                topic.getStatus(),
                topic.getJlptLevel(),
                topic.getOrderIndex(),
                topic.getCoverImageFileId()
        );
    }
}

