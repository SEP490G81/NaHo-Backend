package org.naho.user.usecase;

import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.mapper.UserResultMapper;
import org.naho.user.model.User;
import org.naho.user.port.in.CrudUserInputPort;
import org.naho.user.port.out.RoleRepositoryPort;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.result.UserResult;

public class CrudUserUseCase implements CrudUserInputPort {

    private final UserRepositoryPort userRepositoryPort;

    private final UserResultMapper userResultMapper;
    private final RoleRepositoryPort roleRepositoryPort;

    public CrudUserUseCase(
            UserRepositoryPort userRepositoryPort,
            UserResultMapper userResultMapper,
            RoleRepositoryPort roleRepositoryPort
    ) {
        this.userRepositoryPort = userRepositoryPort;
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
}
