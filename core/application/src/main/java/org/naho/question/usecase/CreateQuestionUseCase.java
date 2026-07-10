package org.naho.question.usecase;

import org.naho.book.exception.TopicErrorCode;
import org.naho.book.port.out.ObjectiveRepositoryPort;
import org.naho.book.util.MarkupParserUtil;
import org.naho.i18n.message.book.ObjectiveDetailMessageKey;
import org.naho.i18n.message.question.QuestionDetailMessageKey;
import org.naho.question.command.CreateQuestionCommand;
import org.naho.question.exception.QuestionErrorCode;
import org.naho.question.model.Question;
import org.naho.question.port.in.CreateQuestionInputPort;
import org.naho.question.port.out.QuestionRepositoryPort;
import org.naho.question.result.CreateQuestionResult;
import org.naho.question.type.QuestionStatus;
import org.naho.shared.exception.ApplicationException;

public class CreateQuestionUseCase implements CreateQuestionInputPort {

    private final QuestionRepositoryPort questionRepositoryPort;
    private final ObjectiveRepositoryPort objectiveRepositoryPort;

    public CreateQuestionUseCase(QuestionRepositoryPort questionRepositoryPort,
                                 ObjectiveRepositoryPort objectiveRepositoryPort) {
        this.questionRepositoryPort = questionRepositoryPort;
        this.objectiveRepositoryPort = objectiveRepositoryPort;
    }

    @Override
    public CreateQuestionResult createQuestion(CreateQuestionCommand command) {
        // 1. Verify Objective exists if objectiveId is provided
        if (command.objectiveId() != null) {
            boolean exists = objectiveRepositoryPort.existsById(command.objectiveId());
            if (!exists) {
                throw new ApplicationException(
                        TopicErrorCode.OBJECTIVE_NOT_FOUND,
                        ObjectiveDetailMessageKey.OBJECTIVE_ID_NOT_FOUND,
                        command.objectiveId()
                );
            }
        }

        // 2. Extract raw text from markup
        String rawTitle = MarkupParserUtil.extractRawTextFromMarkup(command.titleMarkup());
        String rawDescription = MarkupParserUtil.extractRawTextFromMarkup(command.descriptionMarkup());

        // 2.5. Check duplicate title in same objective (if objectiveId is provided)
        if (command.objectiveId() != null && questionRepositoryPort.existsByObjectiveIdAndTitle(command.objectiveId(), rawTitle)) {
            throw new ApplicationException(
                    QuestionErrorCode.QUESTION_TITLE_ALREADY_EXISTS,
                    QuestionDetailMessageKey.QUESTION_TITLE_ALREADY_EXISTS,
                    rawTitle
            );
        }

        // 3. Logic for Order Index
        Double orderIndex = command.orderIndex();
        if (orderIndex == null) {
            if (command.objectiveId() != null) {
                Double maxOrderIndex = questionRepositoryPort.getMaxOrderIndexByObjectiveId(command.objectiveId());
                orderIndex = (maxOrderIndex != null) ? maxOrderIndex + 1.0 : 1.0;
            } else {
                orderIndex = 1.0; // Default if no objective
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
                .objectiveId(command.objectiveId())
                .userId(command.userId())
                .titleMarkup(command.titleMarkup())
                .descriptionMarkup(command.descriptionMarkup())
                .title(rawTitle)
                .description(rawDescription)
                .orderIndex(orderIndex)
                .status(initialStatus)
                .build();

        // 6. Save and Return
        Question savedQuestion = questionRepositoryPort.save(question);

        return new CreateQuestionResult(
                savedQuestion.getId(),
                savedQuestion.getObjectiveId(),
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
