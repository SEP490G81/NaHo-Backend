package org.naho.user.usecase;

import org.naho.file.port.in.CrudFileInputPort;
import org.naho.file.port.out.FileValidatorPort;
import org.naho.file.result.FileResult;
import org.naho.file.result.StoredFile;
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

public class CrudUserUseCase implements CrudUserInputPort {
    private final UserRepositoryPort userRepositoryPort;
    private final FileValidatorPort fileValidatorPort;
    private final CrudFileInputPort crudFileInputPort;
    private final UserResultMapper userResultMapper;
    private final RoleRepositoryPort roleRepositoryPort;

    public CrudUserUseCase(
            UserRepositoryPort userRepositoryPort,
            FileValidatorPort fileValidatorPort,
            CrudFileInputPort crudFileInputPort,
            UserResultMapper userResultMapper,
            RoleRepositoryPort roleRepositoryPort
    ) {
        this.userRepositoryPort = userRepositoryPort;
        this.fileValidatorPort = fileValidatorPort;
        this.crudFileInputPort = crudFileInputPort;
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
        if (command == null) {
            throw new ApplicationException(
                    UserErrorCode.USER_NOT_FOUND,
                    UserDetailMessageKey.USER_ID_NULL
            );
        }
        if (command.id() == null) {
            throw new ApplicationException(
                    UserErrorCode.USER_NOT_FOUND,
                    UserDetailMessageKey.USER_ID_NULL
            );
        }
        User savedUser = userRepositoryPort.updateUserInfo(command);
        return userResultMapper.domainToResult(savedUser);
    }

    /**
     * Method cập nhật avatar của người dùng
     *
     * @param id         user id
     * @param storedFile avatar file mới
     * @return FileResult
     */
    @Override
    public FileResult updateUserAvatar(Long id, StoredFile storedFile) {
        if (id == null) {
            throw new ApplicationException(
                    UserErrorCode.USER_NOT_FOUND,
                    UserDetailMessageKey.USER_ID_NULL
            );
        }
        User user = userRepositoryPort.findById(id)
                .orElseThrow(() -> new ApplicationException(
                        UserErrorCode.USER_NOT_FOUND,
                        UserDetailMessageKey.USER_ID_NOT_FOUND,
                        id
                ));


        return null;
    }
}
