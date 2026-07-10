package org.naho.book.usecase;

import org.naho.book.command.CreateTopicCommand;
import org.naho.book.exception.TopicErrorCode;
import org.naho.book.model.Topic;
import org.naho.book.port.in.CreateTopicInputPort;
import org.naho.book.port.out.TopicRepositoryPort;
import org.naho.book.result.CreateTopicResult;
import org.naho.book.type.TopicStatus;
import org.naho.book.util.MarkupParserUtil;
import org.naho.i18n.message.book.TopicDetailMessageKey;
import org.naho.shared.exception.ApplicationException;

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

        // Validation for duplicate in Book
        if (topicRepositoryPort.existsByJapaneseNameAndBookId(rawName, command.bookId())) {
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
                .bookId(command.bookId())
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
                savedTopic.getOrderIndex(),
                savedTopic.getCoverImageFileId()
        );
    }
}
