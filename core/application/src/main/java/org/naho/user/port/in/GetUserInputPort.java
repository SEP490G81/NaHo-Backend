package org.naho.user.port.in;

import org.naho.user.result.UserResult;

import java.util.List;

public interface GetUserInputPort {
    UserResult getUserById(Long userId);

    UserResult getUserByUserName(String userName);
}
