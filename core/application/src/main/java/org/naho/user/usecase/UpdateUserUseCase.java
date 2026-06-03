package org.naho.user.usecase;

import org.naho.file.port.out.FileRepositoryPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.user.constant.UserApplicationMessageKey;
import org.naho.user.exception.UserApplicationErrorCode;
import org.naho.user.mapper.UserResultMapper;
import org.naho.user.model.User;
import org.naho.user.port.in.UpdateUserInputPort;
import org.naho.user.port.out.RoleRepositoryPort;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.result.UserResult;
import org.naho.user.type.UserStatus;

import java.util.List;

public class UpdateUserUseCase implements UpdateUserInputPort {

    private final UserRepositoryPort userRepositoryPort;
    private final UserResultMapper userResultMapper;
    private final RoleRepositoryPort roleRepositoryPort;
    private final FileRepositoryPort fileRepositoryPort;

    public UpdateUserUseCase(
            UserRepositoryPort userRepositoryPort,
            UserResultMapper userResultMapper,
            RoleRepositoryPort roleRepositoryPort,
            FileRepositoryPort fileRepositoryPort
    ) {
        this.userRepositoryPort = userRepositoryPort;
        this.userResultMapper = userResultMapper;
        this.roleRepositoryPort = roleRepositoryPort;
        this.fileRepositoryPort = fileRepositoryPort;
    }

    @Override
    public UserResult updateStatus(Long id, String status) {
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

        User updatedUser = userRepositoryPort.save(user);

        List<String> roleNames = roleRepositoryPort.findRoleNamesByUserId(user.getId());
        String avatarObjectKey = fileRepositoryPort.findObjectKeyById(user.getAvatarFileId());

        return userResultMapper.domainToResult(updatedUser, roleNames, avatarObjectKey);
    }
}
