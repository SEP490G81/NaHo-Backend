package org.naho.book.usecase;

import org.naho.book.command.GetTopicDetailCommand;
import org.naho.book.exception.TopicErrorCode;
import org.naho.book.model.Topic;
import org.naho.book.port.in.GetTopicDetailInputPort;
import org.naho.book.port.out.TopicRepositoryPort;
import org.naho.book.result.TopicDetailResult;
import org.naho.i18n.message.book.TopicDetailMessageKey;
import org.naho.shared.exception.ApplicationException;

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
                topic.getOrderIndex(),
                topic.getCoverImageFileId()
        );
    }
}
