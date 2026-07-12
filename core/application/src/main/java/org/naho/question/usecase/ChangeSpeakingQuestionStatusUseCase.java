package org.naho.question.usecase;

import org.naho.i18n.message.question.SpeakingQuestionDetailMessageKey;
import org.naho.question.command.ChangeSpeakingQuestionStatusCommand;
import org.naho.question.event.SpeakingQuestionSubmittedEvent;
import org.naho.question.exception.SpeakingQuestionErrorCode;
import org.naho.question.model.SpeakingQuestion;
import org.naho.question.port.in.ChangeSpeakingQuestionStatusInputPort;
import org.naho.question.port.out.SpeakingQuestionRepositoryPort;
import org.naho.question.type.QuestionStatus;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.EventPublisherPort;
import org.naho.shared.port.out.TransactionPort;

import java.util.Optional;

public class ChangeSpeakingQuestionStatusUseCase implements ChangeSpeakingQuestionStatusInputPort {

    private final SpeakingQuestionRepositoryPort speakingQuestionRepositoryPort;
    private final EventPublisherPort eventPublisherPort;
    private final TransactionPort transactionPort;

    public ChangeSpeakingQuestionStatusUseCase(SpeakingQuestionRepositoryPort speakingQuestionRepositoryPort,
                                               EventPublisherPort eventPublisherPort,
                                               TransactionPort transactionPort) {
        this.speakingQuestionRepositoryPort = speakingQuestionRepositoryPort;
        this.eventPublisherPort = eventPublisherPort;
        this.transactionPort = transactionPort;
    }


    @Override
    public void changeSpeakingQuestionStatus(ChangeSpeakingQuestionStatusCommand command) {
        transactionPort.execute(() -> {
            Optional<SpeakingQuestion> questionOpt = speakingQuestionRepositoryPort.findById(command.questionId());
            if (questionOpt.isEmpty()) {
                throw new ApplicationException(
                        SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND,
                        SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_NOT_FOUND,
                        command.questionId()
                );
            }

            SpeakingQuestion speakingQuestion = questionOpt.get();
            QuestionStatus currentStatus = speakingQuestion.getStatus();
            QuestionStatus newStatus = command.newStatus();

            // 1. Learner Submits Question for Review
            if (newStatus == QuestionStatus.PENDING_REVIEW) {
                // Must be owner
                if (!speakingQuestion.getUserId().equals(command.userId())) {
                    throw new ApplicationException(
                            SpeakingQuestionErrorCode.SPEAKING_QUESTION_UPDATE_FORBIDDEN,
                            SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_UPDATE_FORBIDDEN,
                            command.questionId()
                    );
                }

                // Only PRIVATE or REJECTED can be submitted
                // Wait, the rule says REJECTED cannot be edited or resubmitted if it has answers.
                // Let's enforce the strict rule: if REJECTED, it cannot be resubmitted.
                if (currentStatus != QuestionStatus.PRIVATE) {
                    throw new ApplicationException(
                            SpeakingQuestionErrorCode.SPEAKING_QUESTION_UPDATE_FORBIDDEN,
                            SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_UPDATE_FORBIDDEN,
                            command.questionId()
                    );
                }

                speakingQuestion.changeStatus(QuestionStatus.PENDING_REVIEW);
                speakingQuestionRepositoryPort.save(speakingQuestion);

                // Publish Domain Event for Loose Coupling (e.g., Email Notification listener)
                eventPublisherPort.publish(new SpeakingQuestionSubmittedEvent(speakingQuestion.getId(), speakingQuestion.getUserId()));
                return null;
            }

            // 2. Content Manager Reviews Question
            if (newStatus == QuestionStatus.PUBLISHED || newStatus == QuestionStatus.REJECTED) {
                if (!command.isAdminOrManager()) {
                    throw new ApplicationException(
                            SpeakingQuestionErrorCode.SPEAKING_QUESTION_UPDATE_FORBIDDEN,
                            SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_UPDATE_FORBIDDEN,
                            command.questionId()
                    );
                }

                if (currentStatus != QuestionStatus.PENDING_REVIEW && currentStatus != QuestionStatus.DRAFT) {
                    throw new ApplicationException(
                            SpeakingQuestionErrorCode.SPEAKING_QUESTION_UPDATE_FORBIDDEN,
                            SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_UPDATE_FORBIDDEN,
                            command.questionId()
                    );
                }

                speakingQuestion.changeStatus(newStatus);
                // In a real scenario, we might save rejectReason somewhere if REJECTED
                speakingQuestionRepositoryPort.save(speakingQuestion);
                return null;
            }

            // Other transitions (like DRAFT -> PUBLISHED directly by Manager) are handled above

            throw new ApplicationException(
                    SpeakingQuestionErrorCode.SPEAKING_QUESTION_UPDATE_FORBIDDEN,
                    SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_UPDATE_FORBIDDEN,
                    command.questionId()
            );
        });
    }
}
