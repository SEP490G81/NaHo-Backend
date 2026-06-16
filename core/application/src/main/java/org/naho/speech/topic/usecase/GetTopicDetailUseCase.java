package org.naho.speech.topic.usecase;

import org.naho.i18n.message.speech.TopicDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.topic.command.GetTopicDetailCommand;
import org.naho.speech.topic.exception.TopicErrorCode;
import org.naho.speech.topic.port.in.GetTopicDetailInputPort;
import org.naho.speech.topic.port.out.TopicRepositoryPort;
import org.naho.speech.topic.result.TopicDetailResult;
import org.naho.topic.model.Topic;

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
                topic.getDescription(),
                topic.getJapaneseNameTokens(),
                topic.getJapaneseDescriptionTokens(),
                topic.getStatus(),
                topic.getJlptLevel(),
                topic.getOrderIndex(),
                topic.getCoverImageFileId()
        );
    }
}
