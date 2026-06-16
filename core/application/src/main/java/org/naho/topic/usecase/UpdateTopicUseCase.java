package org.naho.topic.usecase;

import org.naho.i18n.message.speech.TopicDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.topic.command.UpdateTopicCommand;
import org.naho.topic.exception.TopicErrorCode;
import org.naho.topic.model.Topic;
import org.naho.topic.port.in.UpdateTopicInputPort;
import org.naho.topic.port.out.TopicRepositoryPort;
import org.naho.topic.result.TopicDetailResult;
import org.naho.topic.util.MarkupParserUtil;

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
        String rawName = MarkupParserUtil.extractRawTextFromMarkup(command.nameMarkup());
        String rawDescription = MarkupParserUtil.extractRawTextFromMarkup(command.descriptionMarkup());

        if (topicRepositoryPort.existsByNameAndJlptLevelExcludeId(rawName, command.jlptLevel(), command.id())) {
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
                command.nameMarkup(),
                command.descriptionMarkup(),
                command.status(),
                command.jlptLevel(),
                orderIndex,
                command.coverImageFileId(),
                command.categoryId()
        );

        Topic savedTopic = topicRepositoryPort.save(topic);

        return new TopicDetailResult(
                savedTopic.getId(),
                savedTopic.getUserId(),
                savedTopic.getName(),
                savedTopic.getDescription(),
                savedTopic.getJapaneseNameMarkup(),
                savedTopic.getJapaneseDescriptionMarkup(),
                savedTopic.getStatus(),
                savedTopic.getJlptLevel(),
                savedTopic.getOrderIndex(),
                savedTopic.getCoverImageFileId(),
                savedTopic.getCategoryId()
        );
    }
}
