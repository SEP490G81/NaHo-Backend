package org.naho.user.usecase;

import org.naho.file.port.out.FileRepositoryPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.user.constant.UserApplicationMessageKey;
import org.naho.user.exception.UserApplicationErrorCode;
import org.naho.user.mapper.UserResultMapper;
import org.naho.user.model.User;
import org.naho.user.port.in.GetUserInputPort;
import org.naho.user.port.out.RoleRepositoryPort;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.result.UserResult;

import java.util.List;

public class GetUserUseCase implements GetUserInputPort {

    private final UserRepositoryPort userRepositoryPort;
    private final RoleRepositoryPort roleRepositoryPort;
    private final FileRepositoryPort fileRepositoryPort;
    private final UserResultMapper userResultMapper;

    public GetUserUseCase(
            UserRepositoryPort userRepositoryPort,
            RoleRepositoryPort roleRepositoryPort,
            FileRepositoryPort fileRepositoryPort,
            UserResultMapper userResultMapper
    ) {
        this.userRepositoryPort = userRepositoryPort;
        this.roleRepositoryPort = roleRepositoryPort;
        this.fileRepositoryPort = fileRepositoryPort;
        this.userResultMapper = userResultMapper;
    }

    @Override
    public List<UserResult> getListUsers() {
        List<User> listUsers = userRepositoryPort.getListUser();
        return listUsers.stream().map(this::mapToResult).toList();
    }

    @Override
    public UserResult getUserById(Long userId) {
        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new ApplicationException(
                        UserApplicationErrorCode.USER_NOT_FOUND,
                        UserApplicationMessageKey.USER_GET_FAILED
                ));
        return mapToResult(user);
    }

    @Override
    public List<UserResult> searchUsers(String userNameOrEmail, String role, String status, String jlptLevel) {
        List<User> users = userRepositoryPort.findByFilters(userNameOrEmail, role, status, jlptLevel);
        return users.stream().map(this::mapToResult).toList();
    }

    @Override
    public UserResult getUserByUserName(String userName) {
        User user = userRepositoryPort.findByUsername(userName)
                .orElseThrow(() -> new ApplicationException(
                        UserApplicationErrorCode.USER_NOT_FOUND,
                        UserApplicationMessageKey.USER_GET_FAILED
                ));
        return mapToResult(user);
    }

    private UserResult mapToResult(User user) {
        List<String> roleNames = roleRepositoryPort.findRoleNamesByUserId(user.getId());
        String avatarFileUrl = fileRepositoryPort.findFileUrlById(user.getAvatarFileId());
        return userResultMapper.domainToResult(user, roleNames, avatarFileUrl);
    }
}
