package org.naho.file.adapter;

import org.naho.file.command.FileUploadCommand;
import org.naho.file.exception.FileErrorCode;
import org.naho.file.port.out.FileValidatorPort;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.exception.InfrastructureException;
import org.springframework.stereotype.Component;

@Component
public class FileValidatorAdapter implements FileValidatorPort {
    @Override
    public void validateFileUploadCommand(FileUploadCommand command) {
        if (command == null || command.getInputStream() == null) {
            throw new InfrastructureException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_EMPTY
            );
        }

        if (command.getOriginalName() == null || command.getOriginalName().isBlank()) {
            throw new InfrastructureException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_ORIGINAL_NAME_EMPTY
            );
        }

        if (command.getFolderName() == null || command.getFolderName().isBlank()) {
            throw new InfrastructureException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_FOLDER_NAME_EMPTY
            );
        }

        if (command.getSize() == null || command.getSize() <= 0) {
            throw new InfrastructureException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_EMPTY
            );
        }
    }
}
