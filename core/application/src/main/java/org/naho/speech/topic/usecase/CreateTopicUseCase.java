package org.naho.speech.topic.usecase;

import org.naho.i18n.message.speech.TopicDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.topic.command.CreateTopicCommand;
import org.naho.speech.topic.exception.TopicErrorCode;
import org.naho.speech.topic.port.in.CreateTopicInputPort;
import org.naho.speech.topic.port.out.TopicRepositoryPort;
import org.naho.speech.topic.result.CreateTopicResult;
import org.naho.topic.model.Topic;
import org.naho.topic.type.TopicStatus;

public class CreateTopicUseCase implements CreateTopicInputPort {

    private final TopicRepositoryPort topicRepositoryPort;

    public CreateTopicUseCase(TopicRepositoryPort topicRepositoryPort) {
        this.topicRepositoryPort = topicRepositoryPort;
    }

    @Override
    public CreateTopicResult createTopic(CreateTopicCommand command) {
        // Validation for duplicate in JLPT level
        if (topicRepositoryPort.existsByJapaneseNameAndJlptLevel(command.japaneseName(), command.jlptLevel())) {
            throw new ApplicationException(TopicErrorCode.TOPIC_ALREADY_EXISTS, TopicDetailMessageKey.TOPIC_ALREADY_EXISTS_IN_LEVEL);
        }

        // Logic for Order Index
        Double orderIndex = command.orderIndex();
        if (orderIndex == null) {
            Double maxOrderIndex = topicRepositoryPort.getMaxOrderIndex();
            orderIndex = (maxOrderIndex != null) ? maxOrderIndex + 1.0 : 1.0;
        } else if (orderIndex < 0) {
            throw new ApplicationException(TopicErrorCode.TOPIC_ORDER_INDEX_INVALID, TopicDetailMessageKey.TOPIC_ORDER_INDEX_INVALID);
        }

        Topic topic = Topic.builder()
                .userId(command.userId())
                .japaneseName(command.japaneseName())
                .description(command.description())
                .japaneseNameTokens(command.nameTokens())
                .japaneseDescriptionTokens(command.descriptionTokens())
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
                savedTopic.getDescription(),
                savedTopic.getJapaneseNameTokens(),
                savedTopic.getJapaneseDescriptionTokens(),
                savedTopic.getStatus(),
                savedTopic.getJlptLevel(),
                savedTopic.getOrderIndex(),
                savedTopic.getCoverImageFileId()
        );
    }
}
