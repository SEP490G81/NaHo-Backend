package org.naho.user.usecase;

import org.naho.shared.exception.ApplicationException;
import org.naho.user.constant.UserApplicationMessageKey;
import org.naho.user.exception.UserApplicationErrorCode;
import org.naho.user.model.User;
import org.naho.user.port.in.UpdateUserInputPort;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.type.UserStatus;

public class UpdateUserUseCase implements UpdateUserInputPort {

    private final UserRepositoryPort userRepositoryPort;

    public UpdateUserUseCase(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public void updateStatus(Long id, String status) {
        User user = userRepositoryPort.findById(id)
                .orElseThrow(() -> new ApplicationException(
                        UserApplicationErrorCode.USER_NOT_FOUND,
                        UserApplicationMessageKey.USER_ID_NOT_FOUND
                ));

        try {
            UserStatus userStatus = UserStatus.valueOf(status.toUpperCase());
            user.setStatus(userStatus);
        } catch (IllegalArgumentException e) {
            throw new ApplicationException(
                    UserApplicationErrorCode.USER_UPDATE_FAILED,
                    UserApplicationMessageKey.USER_UPDATE_STATUS_FAILED
            );
        }

        userRepositoryPort.save(user);
    }
}
