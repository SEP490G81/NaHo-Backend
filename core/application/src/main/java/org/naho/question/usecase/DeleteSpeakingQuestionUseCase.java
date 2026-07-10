package org.naho.question.usecase;

import org.naho.i18n.message.question.SpeakingQuestionDetailMessageKey;
import org.naho.question.command.DeleteSpeakingQuestionCommand;
import org.naho.question.exception.SpeakingQuestionErrorCode;
import org.naho.question.model.SpeakingQuestion;
import org.naho.question.port.in.DeleteSpeakingQuestionInputPort;
import org.naho.question.port.out.SpeakingQuestionRepositoryPort;
import org.naho.question.type.QuestionStatus;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;

import java.util.Optional;

public class DeleteSpeakingQuestionUseCase implements DeleteSpeakingQuestionInputPort {

    private final SpeakingQuestionRepositoryPort speakingQuestionRepositoryPort;
    private final TransactionPort transactionPort;

    public DeleteSpeakingQuestionUseCase(SpeakingQuestionRepositoryPort speakingQuestionRepositoryPort,
                                         TransactionPort transactionPort) {
        this.speakingQuestionRepositoryPort = speakingQuestionRepositoryPort;
        this.transactionPort = transactionPort;
    }

    @Override
    public void deleteSpeakingQuestion(DeleteSpeakingQuestionCommand command) {
        transactionPort.execute(() -> {
            Optional<SpeakingQuestion> questionOpt = speakingQuestionRepositoryPort.findById(command.id());
            if (questionOpt.isEmpty()) {
                throw new ApplicationException(
                        SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND,
                        SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_NOT_FOUND,
                        command.id()
                );
            }

            SpeakingQuestion speakingQuestion = questionOpt.get();

            // Permission Check (Only admin/manager or creator)
            if (!command.isAdminOrManager() && !speakingQuestion.getUserId().equals(command.userId())) {
                throw new ApplicationException(
                        SpeakingQuestionErrorCode.SPEAKING_QUESTION_DELETE_FORBIDDEN,
                        SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_DELETE_FORBIDDEN,
                        command.id()
                );
            }

            boolean isAnswered = speakingQuestionRepositoryPort.hasSpeakingQuestionBeenAnswered(command.id());

            if (isAnswered) {
                // Soft Delete: Keep relationships, set status to ARCHIVE
                speakingQuestion.changeStatus(QuestionStatus.ARCHIVE);
                speakingQuestionRepositoryPort.save(speakingQuestion);
            } else {
                // Hard Delete: DB adapter should handle cascading delete to relationships
                speakingQuestionRepositoryPort.deleteById(command.id());
            }

            return null;
        });
    }
}
