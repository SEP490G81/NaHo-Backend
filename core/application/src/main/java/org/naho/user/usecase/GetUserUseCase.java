package org.naho.user.usecase;

import org.naho.user.mapper.UserResultMapper;
import org.naho.user.model.User;
import org.naho.user.port.in.GetUserInputPort;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.result.UserResult;

import java.util.List;

public class GetUserUseCase implements GetUserInputPort {

    private final UserRepositoryPort userRepositoryPort;
    private final UserResultMapper userResultMapper;
    public GetUserUseCase(UserRepositoryPort userRepositoryPort, UserResultMapper userResultMapper) {
        this.userRepositoryPort = userRepositoryPort;
        this.userResultMapper = userResultMapper;
    }

    @Override
    public List<UserResult> getListUsers() {
        List<User> listUsers = userRepositoryPort.getListUser();
        return userResultMapper.domainsToResults(listUsers);
    }
}
