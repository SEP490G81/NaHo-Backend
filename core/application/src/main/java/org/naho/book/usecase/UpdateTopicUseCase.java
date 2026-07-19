package org.naho.book.usecase;

import org.naho.book.command.UpdateTopicCommand;
import org.naho.book.exception.TopicErrorCode;
import org.naho.book.model.Topic;
import org.naho.book.port.in.UpdateTopicInputPort;
import org.naho.book.port.out.TopicRepositoryPort;
import org.naho.book.result.TopicDetailResult;
import org.naho.book.util.MarkupParserUtil;
import org.naho.i18n.message.book.TopicDetailMessageKey;
import org.naho.shared.exception.ApplicationException;

public class UpdateTopicUseCase implements UpdateTopicInputPort {
    private final TopicRepositoryPort topicRepositoryPort;

    public UpdateTopicUseCase(TopicRepositoryPort topicRepositoryPort) {
        this.topicRepositoryPort = topicRepositoryPort;
    }

    @Override
    public TopicDetailResult updateTopic(UpdateTopicCommand command) {
        Topic topic = topicRepositoryPort.findById(command.id())
                .orElseThrow(() -> new ApplicationException(
                        TopicErrorCode.TOPIC_NOT_FOUND,
                        TopicDetailMessageKey.TOPIC_ID_NOT_FOUND,
                        command.id()
                ));

        if (!command.isAdminOrManager() && !topic.getUserId().equals(command.requestUserId())) {
            throw new ApplicationException(
                    TopicErrorCode.TOPIC_UPDATE_FORBIDDEN,
                    TopicDetailMessageKey.TOPIC_USER_NOT_HAVE_PERMISSION
            );
        }

        // Extract raw text from markup
        String rawName = MarkupParserUtil.extractRawTextFromMarkup(command.japaneseNameMarkup());
        String rawDescription = MarkupParserUtil.extractRawTextFromMarkup(command.japaneseDescriptionMarkup());

        if (topicRepositoryPort.existsByJapaneseNameAndBookIdExcludeId(rawName, topic.getBookId(), command.id())) {
            throw new ApplicationException(
                    TopicErrorCode.TOPIC_ALREADY_EXISTS,
                    TopicDetailMessageKey.TOPIC_ALREADY_EXISTS_IN_LEVEL
            );
        }

        Double orderIndex = command.orderIndex();
        if (orderIndex == null) {
            orderIndex = topic.getOrderIndex(); // Keep old if null
        } else if (orderIndex < 0) {
            throw new ApplicationException(
                    TopicErrorCode.TOPIC_ORDER_INDEX_INVALID,
                    TopicDetailMessageKey.TOPIC_ORDER_INDEX_INVALID
            );
        }

        topic.update(
                rawName,
                rawDescription,
                command.japaneseNameMarkup(),
                command.japaneseDescriptionMarkup(),
                command.status(),
                orderIndex,
                command.coverImageFileId()
        );

        Topic savedTopic = topicRepositoryPort.save(topic);

        return new TopicDetailResult(
                savedTopic.getId(),
                savedTopic.getUserId(),
                savedTopic.getJapaneseName(),
                savedTopic.getJapaneseDescription(),
                savedTopic.getJapaneseNameMarkup(),
                savedTopic.getJapaneseDescriptionMarkup(),
                savedTopic.getStatus(),
                savedTopic.getOrderIndex(),
                savedTopic.getFirstNodeGlobalOrderIndex(),
                savedTopic.getLastNodeGlobalOrderIndex(),
                savedTopic.getCoverImageFileId(),
                java.util.List.of()
        );
    }
}
