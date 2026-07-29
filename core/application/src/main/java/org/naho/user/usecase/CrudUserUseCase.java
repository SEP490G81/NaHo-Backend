package org.naho.user.usecase;

import org.naho.file.command.FileUploadCommand;
import org.naho.file.port.in.CrudFileInputPort;
import org.naho.file.port.in.FileStorageInputPort;
import org.naho.file.port.out.FileValidatorPort;
import org.naho.file.result.FileResult;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.user.command.UpdateUserInfoCommand;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.mapper.UserResultMapper;
import org.naho.user.model.User;
import org.naho.user.port.in.CrudUserInputPort;
import org.naho.user.port.out.RoleRepositoryPort;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.result.UserResult;
import org.naho.user.valueobject.Username;

public class CrudUserUseCase implements CrudUserInputPort {

    private static final String AVATAR_FILE_FOLDER = "avatars";

    private final UserRepositoryPort userRepositoryPort;
    private final FileValidatorPort fileValidatorPort;
    private final CrudFileInputPort crudFileInputPort;
    private final FileStorageInputPort fileStorageInputPort;
    private final UserResultMapper userResultMapper;
    private final RoleRepositoryPort roleRepositoryPort;

    public CrudUserUseCase(
            UserRepositoryPort userRepositoryPort,
            FileValidatorPort fileValidatorPort,
            CrudFileInputPort crudFileInputPort,
            FileStorageInputPort fileStorageInputPort,
            UserResultMapper userResultMapper,
            RoleRepositoryPort roleRepositoryPort
    ) {
        this.userRepositoryPort = userRepositoryPort;
        this.fileValidatorPort = fileValidatorPort;
        this.crudFileInputPort = crudFileInputPort;
        this.fileStorageInputPort = fileStorageInputPort;
        this.userResultMapper = userResultMapper;
        this.roleRepositoryPort = roleRepositoryPort;
    }

    @Override
    public UserResult findUserById(Long userId) {
        if (userId == null) {
            throw new ApplicationException(
                    UserErrorCode.USER_NOT_FOUND,
                    UserDetailMessageKey.USER_ID_NULL
            );
        }

        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new ApplicationException(
                        UserErrorCode.USER_NOT_FOUND,
                        UserDetailMessageKey.USER_ID_NOT_FOUND,
                        userId
                ));
        return userResultMapper.domainToResult(user);
    }

    @Override
    public UserResult updateUserInfo(UpdateUserInfoCommand command) {
        User user = userRepositoryPort.findById(command.id())
                .orElseThrow(() -> new ApplicationException(
                        UserErrorCode.USER_NOT_FOUND,
                        UserDetailMessageKey.USER_ID_NOT_FOUND,
                        command.id()
                ));

        String commandUsername = command.username();
        if (commandUsername != null && !commandUsername.isBlank()) {
            user.setUsername(Username.of(commandUsername));
        }

        FileUploadCommand commandAvatarFile = command.avatarFile();
        if (commandAvatarFile != null) {
            String contentType = fileValidatorPort.validateImageFile(commandAvatarFile.getInputStream());

            FileResult fileResult = fileStorageInputPort.uploadFile(
                    FileUploadCommand.builder()
                            .folderName(AVATAR_FILE_FOLDER)
                            .originalName(commandAvatarFile.getOriginalName())
                            .contentType(contentType)
                            .size(commandAvatarFile.getSize())
                            .build()
            );

            user.setAvatarFileId(fileResult.id());
        }
        
        return null;
    }
}
