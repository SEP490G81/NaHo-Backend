package org.naho.book.usecase;

import org.naho.book.command.UpdateTopicCommand;
import org.naho.book.exception.TopicErrorCode;
import org.naho.book.model.Topic;
import org.naho.book.port.in.UpdateTopicInputPort;
import org.naho.book.port.out.TopicRepositoryPort;
import org.naho.book.result.TopicDetailResult;
import org.naho.furigana.port.out.FuriganaGenerationPort;
import org.naho.i18n.message.book.TopicDetailMessageKey;
import org.naho.shared.exception.ApplicationException;

public class UpdateTopicUseCase implements UpdateTopicInputPort {
    private final TopicRepositoryPort topicRepositoryPort;
    private final FuriganaGenerationPort furiganaGenerationPort;

    public UpdateTopicUseCase(TopicRepositoryPort topicRepositoryPort, FuriganaGenerationPort furiganaGenerationPort) {
        this.topicRepositoryPort = topicRepositoryPort;
        this.furiganaGenerationPort = furiganaGenerationPort;
    }

    @Override
    public TopicDetailResult updateTopic(UpdateTopicCommand command) {
        Topic topic = topicRepositoryPort.findById(command.id())
                .orElseThrow(() -> new ApplicationException(
                        TopicErrorCode.TOPIC_NOT_FOUND,
                        TopicDetailMessageKey.TOPIC_ID_NOT_FOUND,
                        command.id()
                ));

        if (!command.isAdminOrManager()) {
            throw new ApplicationException(
                    TopicErrorCode.TOPIC_UPDATE_FORBIDDEN,
                    TopicDetailMessageKey.TOPIC_USER_NOT_HAVE_PERMISSION
            );
        }

        if (topicRepositoryPort.existsByJapaneseNameAndBookIdExcludeId(command.japaneseName(), topic.getBookId(), command.id())) {
            throw new ApplicationException(
                    TopicErrorCode.TOPIC_ALREADY_EXISTS,
                    TopicDetailMessageKey.TOPIC_ALREADY_EXISTS_IN_LEVEL
            );
        }

        String japaneseNameMarkup = furiganaGenerationPort.generateFuriganaMarkup(command.japaneseName());
        String japaneseDescriptionMarkup = command.japaneseDescription() != null
                ? furiganaGenerationPort.generateFuriganaMarkup(command.japaneseDescription())
                : null;

        topic.update(
                command.japaneseName(),
                command.japaneseDescription(),
                command.vietnameseDescription(),
                command.englishDescription(),
                japaneseNameMarkup,
                japaneseDescriptionMarkup,
                command.status(),
                topic.getOrderIndex(),
                command.coverImageFileId()
        );

        Topic savedTopic = topicRepositoryPort.save(topic);

        return new TopicDetailResult(
                savedTopic.getId(),
                savedTopic.getUserId(),
                savedTopic.getJapaneseName(),
                savedTopic.getJapaneseDescription(),
                savedTopic.getVietnameseDescription(),
                savedTopic.getEnglishDescription(),
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
