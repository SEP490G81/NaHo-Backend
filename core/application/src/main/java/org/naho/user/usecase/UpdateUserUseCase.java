package org.naho.user.usecase;

import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.mapper.UserResultMapper;
import org.naho.user.model.User;
import org.naho.user.port.in.UpdateUserInputPort;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.result.UserResult;
import org.naho.user.type.UserStatus;

public class UpdateUserUseCase implements UpdateUserInputPort {

    private final UserRepositoryPort userRepositoryPort;
    private final UserResultMapper userResultMapper;

    public UpdateUserUseCase(
            UserRepositoryPort userRepositoryPort,
            UserResultMapper userResultMapper
    ) {
        this.userRepositoryPort = userRepositoryPort;
        this.userResultMapper = userResultMapper;
    }

    @Override
    public UserResult updateStatus(Long id, String status) {
        User user = userRepositoryPort.findById(id)
                .orElseThrow(() -> new ApplicationException(
                        UserErrorCode.USER_NOT_FOUND,
                        UserDetailMessageKey.USER_ID_NOT_FOUND
                ));

        try {
            UserStatus userStatus = UserStatus.valueOf(status.toUpperCase());
            user.setStatus(userStatus);
        } catch (IllegalArgumentException e) {
            throw new ApplicationException(
                    UserErrorCode.USER_PERSIST_FAILED,
                    UserDetailMessageKey.USER_UPDATE_STATUS_FAILED
            );
        }

        User updatedUser = userRepositoryPort.save(user, null);

        return userResultMapper.domainToResult(updatedUser);
    }
}
