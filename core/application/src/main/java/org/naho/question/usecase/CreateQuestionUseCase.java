package org.naho.question.usecase;

import org.naho.i18n.message.question.QuestionDetailMessageKey;
import org.naho.i18n.message.topic.TopicDetailMessageKey;
import org.naho.question.command.CreateQuestionCommand;
import org.naho.question.exception.QuestionErrorCode;
import org.naho.question.model.Question;
import org.naho.question.port.in.CreateQuestionInputPort;
import org.naho.question.port.out.QuestionRepositoryPort;
import org.naho.question.result.CreateQuestionResult;
import org.naho.question.type.QuestionStatus;
import org.naho.shared.exception.ApplicationException;
import org.naho.topic.exception.TopicErrorCode;
import org.naho.topic.model.Topic;
import org.naho.topic.port.out.TopicRepositoryPort;
import org.naho.topic.util.MarkupParserUtil;

import java.util.Optional;

public class CreateQuestionUseCase implements CreateQuestionInputPort {

    private final QuestionRepositoryPort questionRepositoryPort;
    private final TopicRepositoryPort topicRepositoryPort;

    public CreateQuestionUseCase(QuestionRepositoryPort questionRepositoryPort,
                                 TopicRepositoryPort topicRepositoryPort) {
        this.questionRepositoryPort = questionRepositoryPort;
        this.topicRepositoryPort = topicRepositoryPort;
    }

    @Override
    public CreateQuestionResult createQuestion(CreateQuestionCommand command) {
        // 1. Verify Topic exists if topicId is provided
        if (command.topicId() != null) {
            Optional<Topic> topicOpt = topicRepositoryPort.findById(command.topicId());
            if (topicOpt.isEmpty()) {
                throw new ApplicationException(
                        TopicErrorCode.TOPIC_NOT_FOUND,
                        TopicDetailMessageKey.TOPIC_ID_NOT_FOUND,
                        command.topicId()
                );
            }
        }

        // 2. Extract raw text from markup
        String rawTitle = MarkupParserUtil.extractRawTextFromMarkup(command.titleMarkup());
        String rawDescription = MarkupParserUtil.extractRawTextFromMarkup(command.descriptionMarkup());

        // 2.5. Check duplicate title in same topic (if topicId is provided)
        if (command.topicId() != null && questionRepositoryPort.existsByTopicIdAndTitle(command.topicId(), rawTitle)) {
            throw new ApplicationException(
                    QuestionErrorCode.QUESTION_TITLE_ALREADY_EXISTS,
                    QuestionDetailMessageKey.QUESTION_TITLE_ALREADY_EXISTS,
                    rawTitle
            );
        }

        // 3. Logic for Order Index
        Double orderIndex = command.orderIndex();
        if (orderIndex == null) {
            if (command.topicId() != null) {
                Double maxOrderIndex = questionRepositoryPort.getMaxOrderIndexByTopicId(command.topicId());
                orderIndex = (maxOrderIndex != null) ? maxOrderIndex + 1.0 : 1.0;
            } else {
                orderIndex = 1.0; // Default if no topic
            }
        } else if (orderIndex < 0) {
            throw new ApplicationException(
                    QuestionErrorCode.QUESTION_ORDER_INDEX_INVALID,
                    QuestionDetailMessageKey.QUESTION_ORDER_INDEX_INVALID
            );
        }

        // 4. Determine initial status based on Creator Role
        QuestionStatus initialStatus = command.isContentManager() ? QuestionStatus.DRAFT : QuestionStatus.PRIVATE;

        // 5. Build Question
        Question question = Question.builder()
                .topicId(command.topicId())
                .userId(command.userId())
                .titleMarkup(command.titleMarkup())
                .descriptionMarkup(command.descriptionMarkup())
                .title(rawTitle)
                .description(rawDescription)
                .orderIndex(orderIndex)
                .status(initialStatus)
                // questionAudioFileId is intentionally left null as per plan
                .build();

        // 6. Save and Return
        Question savedQuestion = questionRepositoryPort.save(question);

        return new CreateQuestionResult(
                savedQuestion.getId(),
                savedQuestion.getTopicId(),
                savedQuestion.getUserId(),
                savedQuestion.getTitle(),
                savedQuestion.getTitleMarkup(),
                savedQuestion.getDescription(),
                savedQuestion.getDescriptionMarkup(),
                savedQuestion.getOrderIndex(),
                savedQuestion.getStatus()
        );
    }
}
