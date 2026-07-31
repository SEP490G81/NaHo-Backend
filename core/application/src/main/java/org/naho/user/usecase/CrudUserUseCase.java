package org.naho.user.usecase;

import org.naho.file.command.UploadFileCommand;
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
import org.naho.user.type.Gender;
import org.naho.user.valueobject.Dob;
import org.naho.user.valueobject.Username;

import java.time.LocalDate;

public class CrudUserUseCase implements CrudUserInputPort {
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
        if (command.id() == null) {
            throw new ApplicationException(
                    UserErrorCode.USER_NOT_FOUND,
                    UserDetailMessageKey.USER_ID_NULL
            );
        }

        User user = userRepositoryPort.findById(command.id())
                .orElseThrow(() -> new ApplicationException(
                        UserErrorCode.USER_NOT_FOUND,
                        UserDetailMessageKey.USER_ID_NOT_FOUND,
                        command.id()
                ));

        // tên người dùng muốn đổi sang
        String commandUsername = command.username();

        if (commandUsername != null && !commandUsername.isBlank()) {
            // tên người dùng hiện tại
            String currentUsername = user.getUsername() != null ? user.getUsername().getValue() : null;

            // nếu tên người dùng muốn đổi sang khác tên hiện tại
            // và đã tồn tại trong db thì ném ra lỗi
            if (!commandUsername.equals(currentUsername) && userRepositoryPort.existsByUsername(commandUsername)) {
                throw new ApplicationException(
                        UserErrorCode.USER_ALREADY_EXISTS,
                        UserDetailMessageKey.USER_USERNAME_ALREADY_EXISTS,
                        commandUsername
                );
            }
            user.setUsername(Username.of(commandUsername));
        }

        UploadFileCommand commandAvatarFile = command.avatarFile();
        if (commandAvatarFile != null) {
            FileResult fileResult = fileStorageInputPort.uploadFile(commandAvatarFile);
            user.setAvatarFileId(fileResult.id());
        }

        String commandFullName = command.fullName();
        if (commandFullName != null && !commandFullName.isBlank()) {
            user.setFullName(commandFullName);
        }

        Gender commandGender = command.gender();
        if (commandGender != null) {
            user.setGender(commandGender);
        }

        LocalDate commandDob = command.dob();
        if (commandDob != null) {
            user.setDob(Dob.of(commandDob));
        }

        User savedUser = userRepositoryPort.save(user);
        return userResultMapper.domainToResult(savedUser);
    }
}
