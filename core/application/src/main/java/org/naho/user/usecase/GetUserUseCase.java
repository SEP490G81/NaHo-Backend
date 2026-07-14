package org.naho.user.usecase;

import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.mapper.UserResultMapper;
import org.naho.user.model.User;
import org.naho.user.port.in.GetUserInputPort;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.result.UserResult;

import java.util.List;

public class GetUserUseCase implements GetUserInputPort {

    private final UserRepositoryPort userRepositoryPort;
    private final UserResultMapper userResultMapper;

    public GetUserUseCase(
            UserRepositoryPort userRepositoryPort,
            UserResultMapper userResultMapper
    ) {
        this.userRepositoryPort = userRepositoryPort;
        this.userResultMapper = userResultMapper;
    }

    @Override
    public List<UserResult> getListUsers() {
        List<User> listUsers = userRepositoryPort.getListUser();
        return listUsers.stream().map(userResultMapper::domainToResult).toList();
    }

    @Override
    public UserResult getUserById(Long userId) {
        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new ApplicationException(
                        UserErrorCode.USER_NOT_FOUND,
                        UserDetailMessageKey.USER_GET_FAILED
                ));
        return userResultMapper.domainToResult(user);
    }

    @Override
    public List<UserResult> searchUsers(String userNameOrEmail, String role, String status, String jlptLevel) {
        List<User> users = userRepositoryPort.findByFilters(userNameOrEmail, role, status, jlptLevel);
        return users.stream().map(userResultMapper::domainToResult).toList();
    }

    @Override
    public UserResult getUserByUserName(String userName) {
        User user = userRepositoryPort.findByUsername(userName)
                .orElseThrow(() -> new ApplicationException(
                        UserErrorCode.USER_NOT_FOUND,
                        UserDetailMessageKey.USER_GET_FAILED
                ));
        return userResultMapper.domainToResult(user);
    }
}
