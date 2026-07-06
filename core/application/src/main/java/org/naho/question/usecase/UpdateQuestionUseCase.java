package org.naho.question.usecase;

import org.naho.i18n.message.question.QuestionDetailMessageKey;
import org.naho.question.command.UpdateQuestionCommand;
import org.naho.question.exception.QuestionErrorCode;
import org.naho.question.model.Question;
import org.naho.question.port.in.UpdateQuestionInputPort;
import org.naho.question.port.out.QuestionRepositoryPort;
import org.naho.question.result.UpdateQuestionResult;
import org.naho.shared.exception.ApplicationException;
import org.naho.topic.util.MarkupParserUtil;

import java.util.Optional;

public class UpdateQuestionUseCase implements UpdateQuestionInputPort {

    private final QuestionRepositoryPort questionRepositoryPort;

    public UpdateQuestionUseCase(QuestionRepositoryPort questionRepositoryPort) {
        this.questionRepositoryPort = questionRepositoryPort;
    }

    @Override
    public UpdateQuestionResult updateQuestion(UpdateQuestionCommand command) {
        // 1. Verify Question exists
        Optional<Question> questionOpt = questionRepositoryPort.findById(command.id());
        if (questionOpt.isEmpty()) {
            throw new ApplicationException(
                    QuestionErrorCode.QUESTION_NOT_FOUND,
                    QuestionDetailMessageKey.QUESTION_NOT_FOUND,
                    command.id()
            );
        }

        Question question = questionOpt.get();

        // 2. Check update rule: Cannot update if already answered
        boolean isAnswered = questionRepositoryPort.hasQuestionBeenAnswered(command.id());
        if (isAnswered) {
            throw new ApplicationException(
                    QuestionErrorCode.QUESTION_UPDATE_FORBIDDEN,
                    QuestionDetailMessageKey.QUESTION_UPDATE_FORBIDDEN,
                    command.id()
            );
        }

        // 3. Extract raw text from markup
        String rawTitle = MarkupParserUtil.extractRawTextFromMarkup(command.titleMarkup());
        String rawDescription = MarkupParserUtil.extractRawTextFromMarkup(command.descriptionMarkup());

        // 3.5. Check duplicate title in same objective (if objectiveId exists)
        if (question.getObjectiveId() != null && questionRepositoryPort.existsByObjectiveIdAndTitleExcludeId(question.getObjectiveId(), rawTitle, command.id())) {
            throw new ApplicationException(
                    QuestionErrorCode.QUESTION_TITLE_ALREADY_EXISTS,
                    QuestionDetailMessageKey.QUESTION_TITLE_ALREADY_EXISTS,
                    rawTitle
            );
        }

        // 4. Handle Order Index logic
        Double orderIndex = command.orderIndex();
        if (orderIndex != null && orderIndex < 0) {
            throw new ApplicationException(
                    QuestionErrorCode.QUESTION_ORDER_INDEX_INVALID,
                    QuestionDetailMessageKey.QUESTION_ORDER_INDEX_INVALID
            );
        }

        if (orderIndex == null) {
            orderIndex = question.getOrderIndex();
        }

        // 5. Update Domain Model
        question.update(
                rawTitle,
                rawDescription,
                command.titleMarkup(),
                command.descriptionMarkup(),
                orderIndex,
                null // Audio is null for now
        );

        // 6. Save and Return
        Question updatedQuestion = questionRepositoryPort.save(question);

        return new UpdateQuestionResult(
                updatedQuestion.getId(),
                updatedQuestion.getObjectiveId(),
                updatedQuestion.getUserId(),
                updatedQuestion.getTitle(),
                updatedQuestion.getTitleMarkup(),
                updatedQuestion.getDescription(),
                updatedQuestion.getDescriptionMarkup(),
                updatedQuestion.getOrderIndex(),
                updatedQuestion.getStatus()
        );
    }
}
