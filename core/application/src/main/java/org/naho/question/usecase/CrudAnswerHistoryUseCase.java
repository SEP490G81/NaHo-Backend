package org.naho.question.usecase;

import org.naho.file.exception.FileErrorCode;
import org.naho.file.model.File;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.file.type.OperationStatus;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.i18n.message.question.SpeakingQuestionDetailMessageKey;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.question.exception.SpeakingQuestionErrorCode;
import org.naho.question.port.in.CrudAnswerHistoryInputPort;
import org.naho.question.port.out.AnswerHistoryRepositoryPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.model.AnswerHistory;
import org.naho.user.exception.UserErrorCode;

public class CrudAnswerHistoryUseCase implements CrudAnswerHistoryInputPort {
    private final AnswerHistoryRepositoryPort answerHistoryRepositoryPort;
    private final FileRepositoryPort fileRepositoryPort;
    private final FileStorageServicePort fileStorageServicePort;

    public CrudAnswerHistoryUseCase(
            AnswerHistoryRepositoryPort answerHistoryRepositoryPort,
            FileRepositoryPort fileRepositoryPort,
            FileStorageServicePort fileStorageServicePort
    ) {
        this.answerHistoryRepositoryPort = answerHistoryRepositoryPort;
        this.fileRepositoryPort = fileRepositoryPort;
        this.fileStorageServicePort = fileStorageServicePort;
    }

    @Override
    public String generateAudioFilePresignedUrl(Long id, Long userId) {
        if (id == null) {
            throw new ApplicationException(
                    SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND,
                    SpeakingQuestionDetailMessageKey.ANSWER_HISTORY_ID_NULL
            );
        }

        AnswerHistory answerHistory = answerHistoryRepositoryPort.findById(id)
                .orElseThrow(() -> new ApplicationException(
                        SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND,
                        SpeakingQuestionDetailMessageKey.ANSWER_HISTORY_NOT_FOUND,
                        id
                ));

        if (!userId.equals(answerHistory.getUserId())) {
            throw new ApplicationException(
                    UserErrorCode.USER_UNAUTHORIZED,
                    UserDetailMessageKey.USER_UNAUTHORIZED
            );
        }

        if (answerHistory.getAudioFileId() == null) {
            throw new ApplicationException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_ID_NULL
            );
        }

        File file = fileRepositoryPort.findById(answerHistory.getAudioFileId());

        if (!OperationStatus.COMPLETED.equals(file.getOperationStatus())) {
            throw new ApplicationException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_NOT_UPLOADED
            );
        }

        return fileStorageServicePort.generatePresignedUrl(file);
    }
}
