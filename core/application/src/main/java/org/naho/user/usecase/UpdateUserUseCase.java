package org.naho.user.usecase;

import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.user.exception.RoleErrorCode;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.mapper.UserResultMapper;
import org.naho.user.model.Role;
import org.naho.user.model.User;
import org.naho.user.port.in.UpdateUserInputPort;
import org.naho.user.port.out.RoleRepositoryPort;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.result.UserResult;
import org.naho.user.type.RoleName;
import org.naho.user.type.UserStatus;

public class UpdateUserUseCase implements UpdateUserInputPort {

    private final UserRepositoryPort userRepositoryPort;
    private final RoleRepositoryPort roleRepositoryPort;
    private final UserResultMapper userResultMapper;

    public UpdateUserUseCase(
            UserRepositoryPort userRepositoryPort,
            RoleRepositoryPort roleRepositoryPort,
            UserResultMapper userResultMapper
    ) {
        this.userRepositoryPort = userRepositoryPort;
        this.roleRepositoryPort = roleRepositoryPort;
        this.userResultMapper = userResultMapper;
    }

    /**
     * ACTIVE/UNACTIVE user status
     *
     * @param id user id
     * @return UserResult
     */
    @Override
    public UserResult updateStatus(Long id) {
        if (id == null) {
            throw new ApplicationException(
                    UserErrorCode.USER_NOT_FOUND,
                    UserDetailMessageKey.USER_ID_NULL
            );
        }

        User user = userRepositoryPort
                .findById(id)
                .orElseThrow(() -> new ApplicationException(
                        UserErrorCode.USER_NOT_FOUND,
                        UserDetailMessageKey.USER_ID_NOT_FOUND,
                        id
                ));

        Role role = roleRepositoryPort.findById(user.getRoleId())
                .orElseThrow(() -> new ApplicationException(
                        RoleErrorCode.ROLE_NOT_FOUND,
                        UserDetailMessageKey.USER_ROLE_NOT_FOUND
                ));

        // nếu tài khoản là ROLE ADMIN thì không cho thay đổi status
        if (RoleName.ADMIN.equals(role.getRoleName())) {
            throw new ApplicationException(
                    UserErrorCode.USER_ACCESS_DENIED,
                    UserDetailMessageKey.USER_ACCESS_DENIED
            );
        }

        UserStatus newStatus;

        // nếu trạng thái là ACTIVE thì set thành UNACTIVE
        if (UserStatus.ACTIVE.equals(user.getStatus())) {
            newStatus = UserStatus.UNACTIVE;
        } else if (UserStatus.UNACTIVE.equals(user.getStatus())) { // nếu trạng thái là UNACTIVE thì set thành ACTIVE
            newStatus = UserStatus.ACTIVE;
        } else {
            throw new ApplicationException(
                    UserErrorCode.USER_PERSIST_FAILED,
                    UserDetailMessageKey.USER_UPDATE_STATUS_FAILED
            );
        }

        User savedUser = userRepositoryPort.updateUserStatus(id, newStatus);
        return userResultMapper.domainToResult(savedUser);
    }
}
