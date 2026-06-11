package org.naho.speech.topic.usecase;

import org.naho.i18n.message.speech.TopicDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.speech.model.Topic;
import org.naho.speech.question.port.out.QuestionRepositoryPort;
import org.naho.speech.topic.command.DeleteTopicCommand;
import org.naho.speech.topic.exception.TopicErrorCode;
import org.naho.speech.topic.port.in.DeleteTopicInputPort;
import org.naho.speech.topic.port.out.TopicRepositoryPort;
import org.naho.speech.type.QuestionStatus;
import org.naho.speech.type.TopicStatus;

import java.util.Optional;

public class DeleteTopicUseCase implements DeleteTopicInputPort {

    private final TopicRepositoryPort topicRepositoryPort;
    private final QuestionRepositoryPort questionRepositoryPort;
    private final TransactionPort transactionPort;

    public DeleteTopicUseCase(TopicRepositoryPort topicRepositoryPort,
                              QuestionRepositoryPort questionRepositoryPort,
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

            boolean hasAnswers = questionRepositoryPort.hasAnyQuestionBeenAnsweredInTopic(command.id());

            if (hasAnswers) {
                // Soft delete
                topic.update(
                        topic.getName(),
                        topic.getDescription(),
                        topic.getJapaneseNameTokens(),
                        topic.getJapaneseDescriptionTokens(),
                        TopicStatus.ARCHIVE,
                        topic.getJlptLevel(),
                        topic.getOrderIndex(),
                        topic.getCoverImageFileId()
                );
                topicRepositoryPort.save(topic);
                questionRepositoryPort.updateQuestionsStatusByTopicId(command.id(), QuestionStatus.ARCHIVE);
                topicRepositoryPort.save(topic);
            } else {
                // Hard delete
                questionRepositoryPort.deleteQuestionsByTopicId(command.id());
                topicRepositoryPort.deleteById(command.id());
            }
            return null;
        });
    }
}
