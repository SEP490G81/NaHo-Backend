package org.naho.question.usecase;

import org.naho.file.exception.FileErrorCode;
import org.naho.file.exception.FileOperationErrorCode;
import org.naho.file.model.File;
import org.naho.file.model.FileOperation;
import org.naho.file.port.out.FileOperationRepositoryPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.file.type.OperationStatus;
import org.naho.file.type.OperationType;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.i18n.message.file.FileOperationDetailMessageKey;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.question.port.in.CrudAnswerHistoryInputPort;
import org.naho.question.port.out.AnswerHistoryRepositoryPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.model.AnswerHistory;
import org.naho.user.exception.UserErrorCode;

public class CrudAnswerHistoryUseCase implements CrudAnswerHistoryInputPort {
    private final AnswerHistoryRepositoryPort answerHistoryRepositoryPort;
    private final FileRepositoryPort fileRepositoryPort;
    private final FileOperationRepositoryPort fileOperationRepositoryPort;
    private final FileStorageServicePort fileStorageServicePort;

    public CrudAnswerHistoryUseCase(
            AnswerHistoryRepositoryPort answerHistoryRepositoryPort,
            FileRepositoryPort fileRepositoryPort,
            FileOperationRepositoryPort fileOperationRepositoryPort,
            FileStorageServicePort fileStorageServicePort
    ) {
        this.answerHistoryRepositoryPort = answerHistoryRepositoryPort;
        this.fileRepositoryPort = fileRepositoryPort;
        this.fileOperationRepositoryPort = fileOperationRepositoryPort;
        this.fileStorageServicePort = fileStorageServicePort;
    }

    @Override
    public String generateAudioFilePresignedUrl(Long id, Long userId) {
        if (id == null) {
            throw new ApplicationException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_ID_NULL
            );
        }

        AnswerHistory answerHistory = answerHistoryRepositoryPort.findById(id)
                .orElseThrow(() -> new ApplicationException(
                        FileErrorCode.FILE_NOT_FOUND,
                        FileDetailMessageKey.FILE_NOT_FOUND,
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
                    FileErrorCode.FILE_NOT_FOUND,
                    FileDetailMessageKey.FILE_NOT_FOUND,
                    id
            );
        }

        File file = fileRepositoryPort.findById(answerHistory.getAudioFileId());

        FileOperation uploadOperation = fileOperationRepositoryPort
                .findByFileIdAndOperationType(file.getId(), OperationType.UPLOAD)
                .orElseThrow(() -> new ApplicationException(
                        FileOperationErrorCode.FILE_OPERATION_NOT_FOUND,
                        FileOperationDetailMessageKey.FILE_OPERATION_NOT_FOUND,
                        file.getId()
                ));

        if (!OperationStatus.COMPLETED.equals(uploadOperation.getOperationStatus())) {
            throw new ApplicationException(
                    FileOperationErrorCode.FILE_OPERATION_NOT_VALID,
                    FileOperationDetailMessageKey.FILE_OPERATION_NOT_VALID
            );
        }

        return fileStorageServicePort.generatePresignedUrl(file);
    }
}
