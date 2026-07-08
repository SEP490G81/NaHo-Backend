package org.naho.topic.usecase;

import org.naho.i18n.message.topic.TopicDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.topic.command.CreateTopicCommand;
import org.naho.topic.exception.TopicErrorCode;
import org.naho.topic.model.Topic;
import org.naho.topic.port.in.CreateTopicInputPort;
import org.naho.topic.port.out.TopicRepositoryPort;
import org.naho.topic.result.CreateTopicResult;
import org.naho.topic.type.TopicStatus;
import org.naho.topic.util.MarkupParserUtil;

public class CreateTopicUseCase implements CreateTopicInputPort {

    private final TopicRepositoryPort topicRepositoryPort;

    public CreateTopicUseCase(TopicRepositoryPort topicRepositoryPort) {
        this.topicRepositoryPort = topicRepositoryPort;
    }

    @Override
    public CreateTopicResult createTopic(CreateTopicCommand command) {
        // Extract raw text from markup
        String rawName = MarkupParserUtil.extractRawTextFromMarkup(command.japaneseNameMarkup());
        String rawDescription = MarkupParserUtil.extractRawTextFromMarkup(command.japaneseDescriptionMarkup());

        // Validation for duplicate in JLPT level
        if (topicRepositoryPort.existsByJapaneseNameAndJlptLevel(rawName, command.jlptLevel())) {
            throw new ApplicationException(
                    TopicErrorCode.TOPIC_ALREADY_EXISTS,
                    TopicDetailMessageKey.TOPIC_ALREADY_EXISTS_IN_LEVEL
            );
        }

        // Logic for Order Index
        Double orderIndex = command.orderIndex();
        if (orderIndex == null) {
            Double maxOrderIndex = topicRepositoryPort.getMaxOrderIndex();
            orderIndex = (maxOrderIndex != null) ? maxOrderIndex + 1.0 : 1.0;
        } else if (orderIndex < 0) {
            throw new ApplicationException(
                    TopicErrorCode.TOPIC_ORDER_INDEX_INVALID,
                    TopicDetailMessageKey.TOPIC_ORDER_INDEX_INVALID
            );
        }

        Topic topic = Topic.builder()
                .userId(command.userId())
                .japaneseName(rawName)
                .japaneseDescription(rawDescription)
                .japaneseNameMarkup(command.japaneseNameMarkup())
                .japaneseDescriptionMarkup(command.japaneseDescriptionMarkup())
                .jlptLevel(command.jlptLevel())
                .orderIndex(orderIndex)
                .coverImageFileId(command.coverImageFileId())
                .status(TopicStatus.DRAFT) // Initial status is DRAFT
                .build();

        Topic savedTopic = topicRepositoryPort.save(topic);

        return new CreateTopicResult(
                savedTopic.getId(),
                savedTopic.getUserId(),
                savedTopic.getJapaneseName(),
                savedTopic.getJapaneseDescription(),
                savedTopic.getJapaneseNameMarkup(),
                savedTopic.getJapaneseDescriptionMarkup(),
                savedTopic.getStatus(),
                savedTopic.getJlptLevel(),
                savedTopic.getOrderIndex(),
                savedTopic.getCoverImageFileId()
        );
    }
}
