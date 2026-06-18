package org.naho.question.usecase;

import org.naho.i18n.message.question.QuestionDetailMessageKey;
import org.naho.question.command.ChangeQuestionStatusCommand;
import org.naho.question.event.QuestionSubmittedEvent;
import org.naho.question.exception.QuestionErrorCode;
import org.naho.question.model.Question;
import org.naho.question.port.in.ChangeQuestionStatusInputPort;
import org.naho.question.port.out.QuestionRepositoryPort;
import org.naho.question.type.QuestionStatus;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.EventPublisherPort;
import org.naho.shared.port.out.TransactionPort;

import java.util.Optional;

public class ChangeQuestionStatusUseCase implements ChangeQuestionStatusInputPort {

    private final QuestionRepositoryPort questionRepositoryPort;
    private final EventPublisherPort eventPublisherPort;
    private final TransactionPort transactionPort;

    public ChangeQuestionStatusUseCase(QuestionRepositoryPort questionRepositoryPort,
                                       EventPublisherPort eventPublisherPort,
                                       TransactionPort transactionPort) {
        this.questionRepositoryPort = questionRepositoryPort;
        this.eventPublisherPort = eventPublisherPort;
        this.transactionPort = transactionPort;
    }


    @Override
    public void changeQuestionStatus(ChangeQuestionStatusCommand command) {
        transactionPort.execute(() -> {
            Optional<Question> questionOpt = questionRepositoryPort.findById(command.questionId());
            if (questionOpt.isEmpty()) {
                throw new ApplicationException(
                        QuestionErrorCode.QUESTION_NOT_FOUND,
                        QuestionDetailMessageKey.QUESTION_NOT_FOUND,
                        command.questionId()
                );
            }

            Question question = questionOpt.get();
            QuestionStatus currentStatus = question.getStatus();
            QuestionStatus newStatus = command.newStatus();

            // 1. Learner Submits Question for Review
            if (newStatus == QuestionStatus.PENDING_REVIEW) {
                // Must be owner
                if (!question.getUserId().equals(command.userId())) {
                    throw new ApplicationException(
                            QuestionErrorCode.QUESTION_UPDATE_FORBIDDEN,
                            QuestionDetailMessageKey.QUESTION_UPDATE_FORBIDDEN,
                            command.questionId()
                    );
                }

                // Only PRIVATE or REJECTED can be submitted
                // Wait, the rule says REJECTED cannot be edited or resubmitted if it has answers.
                // Let's enforce the strict rule: if REJECTED, it cannot be resubmitted.
                if (currentStatus != QuestionStatus.PRIVATE) {
                    throw new ApplicationException(
                            QuestionErrorCode.QUESTION_UPDATE_FORBIDDEN,
                            QuestionDetailMessageKey.QUESTION_UPDATE_FORBIDDEN,
                            command.questionId()
                    );
                }

                question.changeStatus(QuestionStatus.PENDING_REVIEW);
                questionRepositoryPort.save(question);

                // Publish Domain Event for Loose Coupling (e.g., Email Notification listener)
                eventPublisherPort.publish(new QuestionSubmittedEvent(question.getId(), question.getUserId()));
                return null;
            }

            // 2. Content Manager Reviews Question
            if (newStatus == QuestionStatus.PUBLISHED || newStatus == QuestionStatus.REJECTED) {
                if (!command.isAdminOrManager()) {
                    throw new ApplicationException(
                            QuestionErrorCode.QUESTION_UPDATE_FORBIDDEN,
                            QuestionDetailMessageKey.QUESTION_UPDATE_FORBIDDEN,
                            command.questionId()
                    );
                }

                if (currentStatus != QuestionStatus.PENDING_REVIEW && currentStatus != QuestionStatus.DRAFT) {
                    throw new ApplicationException(
                            QuestionErrorCode.QUESTION_UPDATE_FORBIDDEN,
                            QuestionDetailMessageKey.QUESTION_UPDATE_FORBIDDEN,
                            command.questionId()
                    );
                }

                question.changeStatus(newStatus);
                // In a real scenario, we might save rejectReason somewhere if REJECTED
                questionRepositoryPort.save(question);
                return null;
            }

            // Other transitions (like DRAFT -> PUBLISHED directly by Manager) are handled above

            throw new ApplicationException(
                    QuestionErrorCode.QUESTION_UPDATE_FORBIDDEN,
                    QuestionDetailMessageKey.QUESTION_UPDATE_FORBIDDEN,
                    command.questionId()
            );
        });
    }
}
