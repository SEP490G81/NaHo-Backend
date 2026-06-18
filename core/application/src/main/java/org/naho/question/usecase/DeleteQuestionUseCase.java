package org.naho.question.usecase;

import org.naho.i18n.message.question.QuestionDetailMessageKey;
import org.naho.question.command.DeleteQuestionCommand;
import org.naho.question.exception.QuestionErrorCode;
import org.naho.question.model.Question;
import org.naho.question.port.in.DeleteQuestionInputPort;
import org.naho.question.port.out.QuestionRepositoryPort;
import org.naho.question.type.QuestionStatus;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;

import java.util.Optional;

public class DeleteQuestionUseCase implements DeleteQuestionInputPort {

    private final QuestionRepositoryPort questionRepositoryPort;
    private final TransactionPort transactionPort;

    public DeleteQuestionUseCase(QuestionRepositoryPort questionRepositoryPort,
                                 TransactionPort transactionPort) {
        this.questionRepositoryPort = questionRepositoryPort;
        this.transactionPort = transactionPort;
    }

    @Override
    public void deleteQuestion(DeleteQuestionCommand command) {
        transactionPort.execute(() -> {
            Optional<Question> questionOpt = questionRepositoryPort.findById(command.id());
            if (questionOpt.isEmpty()) {
                throw new ApplicationException(
                        QuestionErrorCode.QUESTION_NOT_FOUND,
                        QuestionDetailMessageKey.QUESTION_NOT_FOUND,
                        command.id()
                );
            }

            Question question = questionOpt.get();

            // Permission Check (Only admin/manager or creator)
            if (!command.isAdminOrManager() && !question.getUserId().equals(command.userId())) {
                throw new ApplicationException(
                        QuestionErrorCode.QUESTION_DELETE_FORBIDDEN,
                        QuestionDetailMessageKey.QUESTION_DELETE_FORBIDDEN,
                        command.id()
                );
            }

            boolean isAnswered = questionRepositoryPort.hasQuestionBeenAnswered(command.id());

            if (isAnswered) {
                // Soft Delete: Keep relationships, set status to ARCHIVE
                question.changeStatus(QuestionStatus.ARCHIVE);
                questionRepositoryPort.save(question);
            } else {
                // Hard Delete: DB adapter should handle cascading delete to relationships
                questionRepositoryPort.deleteById(command.id());
            }

            return null;
        });
    }
}
