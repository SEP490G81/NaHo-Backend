package org.naho.user.usecase;

import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.user.exception.RoleErrorCode;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.mapper.UserResultMapper;
import org.naho.user.model.Role;
import org.naho.user.model.User;
import org.naho.user.port.in.UpdateUserInputPort;
import org.naho.user.port.out.RoleRepositoryPort;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.port.out.UserSessionServicePort;
import org.naho.user.type.RoleName;
import org.naho.user.type.SessionRevokedReason;
import org.naho.user.type.UserStatus;

public class UpdateUserUseCase implements UpdateUserInputPort {

    private final UserRepositoryPort userRepositoryPort;
    private final RoleRepositoryPort roleRepositoryPort;
    private final UserResultMapper userResultMapper;
    private final UserSessionServicePort userSessionServicePort;
    private final TransactionPort transactionPort;

    public UpdateUserUseCase(
            UserRepositoryPort userRepositoryPort,
            RoleRepositoryPort roleRepositoryPort,
            UserResultMapper userResultMapper,
            UserSessionServicePort userSessionServicePort,
            TransactionPort transactionPort
    ) {
        this.userRepositoryPort = userRepositoryPort;
        this.roleRepositoryPort = roleRepositoryPort;
        this.userResultMapper = userResultMapper;
        this.userSessionServicePort = userSessionServicePort;
        this.transactionPort = transactionPort;
    }

    /**
     * ACTIVE/UNACTIVE user status
     * nếu UNACTIVE thì xử lí gửi thông báo qua mail và thu hồi toàn bộ phiên đăng nhập
     *
     * @param id user id
     * @return UserStatus
     */
    @Override
    public UserStatus updateStatus(Long id) {
        return transactionPort.execute(() -> doUpdateStatus(id));
    }

    private UserStatus doUpdateStatus(Long id) {
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
                    UserDetailMessageKey.USER_CANNOT_LOCK_ADMIN
            );
        }

        UserStatus newStatus = getNewStatus(user);

        // lưu trạng thái mới của tài khoản
        userRepositoryPort.updateUserStatus(id, newStatus);

        // nếu trạng thái bị cập nhật thành UNACTIVE thì
        // thu hồi toàn bộ phiên đăng nhập của tài khoản đó
        if (UserStatus.UNACTIVE.equals(newStatus)) {
            userSessionServicePort.revokeAllActiveSessionsByUserId(
                    id,
                    SessionRevokedReason.UNACTIVE_ACCOUNT
            );
        }

        return newStatus;
    }

    /**
     * Lấy trạng thái mới của người dùng dựa trên trạng thái hiện tại
     * ACTIVE => UNACTIVE
     * UNACTIVE => ACTIVE
     *
     * @param user người dùng bị chỉnh sửa trạng thái
     * @return new UserStatus
     */
    private UserStatus getNewStatus(User user) {
        UserStatus newStatus;

        // nếu trạng thái là ACTIVE thì set thành UNACTIVE
        if (UserStatus.ACTIVE.equals(user.getStatus())) {
            newStatus = UserStatus.UNACTIVE;
        } else if (UserStatus.UNACTIVE.equals(user.getStatus())) {
            // nếu trạng thái là UNACTIVE thì set thành ACTIVE
            newStatus = UserStatus.ACTIVE;
        } else {
            throw new ApplicationException(
                    UserErrorCode.USER_PERSIST_FAILED,
                    UserDetailMessageKey.USER_UPDATE_STATUS_FAILED
            );
        }
        return newStatus;
    }
}
