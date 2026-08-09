package org.naho.user.port.in;

import org.naho.user.result.UserResult;

import java.util.List;

public interface GetUserInputPort {
    List<UserResult> getListUsers();

    UserResult getUserById(Long userId);

    List<UserResult> searchUsers(String userNameOrEmail, String role, String status);

    UserResult getUserByUserName(String userName);
}
