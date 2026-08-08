package org.naho.book.usecase;

import org.naho.book.command.DeleteTopicCommand;
import org.naho.book.exception.TopicErrorCode;
import org.naho.book.model.Topic;
import org.naho.book.port.in.DeleteTopicInputPort;
import org.naho.book.port.out.TopicRepositoryPort;
import org.naho.book.type.TopicStatus;
import org.naho.i18n.message.book.TopicDetailMessageKey;
import org.naho.question.port.out.SpeakingQuestionRepositoryPort;
import org.naho.question.type.QuestionStatus;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;

import java.util.Optional;

public class DeleteTopicUseCase implements DeleteTopicInputPort {

    private final TopicRepositoryPort topicRepositoryPort;
    private final SpeakingQuestionRepositoryPort questionRepositoryPort;
    private final TransactionPort transactionPort;

    public DeleteTopicUseCase(TopicRepositoryPort topicRepositoryPort,
                              SpeakingQuestionRepositoryPort questionRepositoryPort,
                              TransactionPort transactionPort
    ) {
        this.topicRepositoryPort = topicRepositoryPort;
        this.questionRepositoryPort = questionRepositoryPort;
        this.transactionPort = transactionPort;
    }

    @Override
    public void deleteTopic(DeleteTopicCommand command) {
        transactionPort.execute(() -> {
            if (!command.isAdminOrManager()) {
                throw new ApplicationException(
                        TopicErrorCode.TOPIC_DELETE_FORBIDDEN,
                        TopicDetailMessageKey.TOPIC_USER_NOT_HAVE_PERMISSION
                );
            }

            Optional<Topic> topicOpt = topicRepositoryPort.findById(command.id());
            if (topicOpt.isEmpty()) {
                throw new ApplicationException(
                        TopicErrorCode.TOPIC_NOT_FOUND,
                        TopicDetailMessageKey.TOPIC_ID_NOT_FOUND,
                        command.id()
                );
            }

            Topic topic = topicOpt.get();

            boolean hasAnswers = questionRepositoryPort.hasAnySpeakingQuestionBeenAnsweredInTopic(command.id());

            if (hasAnswers) {
                // Soft delete
                topic.update(
                        topic.getJapaneseName(),
                        topic.getJapaneseDescription(),
                        topic.getVietnameseDescription(),
                        topic.getEnglishDescription(),
                        topic.getJapaneseNameMarkup(),
                        topic.getJapaneseDescriptionMarkup(),
                        TopicStatus.ARCHIVE,
                        topic.getOrderIndex(),
                        topic.getCoverImageFileId()
                );
                topicRepositoryPort.save(topic);
                questionRepositoryPort.updateSpeakingQuestionsStatusByTopicId(command.id(), QuestionStatus.ARCHIVE);
            } else {
                // Hard delete
                questionRepositoryPort.deleteSpeakingQuestionsByTopicId(command.id());
                topicRepositoryPort.deleteById(command.id());
            }
            return null;
        });
    }
}
