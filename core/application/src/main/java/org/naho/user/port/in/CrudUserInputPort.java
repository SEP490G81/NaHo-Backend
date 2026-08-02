package org.naho.user.port.in;

import org.naho.user.command.UpdateUserInfoCommand;
import org.naho.user.result.UserResult;

public interface CrudUserInputPort {
    UserResult findUserById(Long id);

    UserResult updateUserInfo(UpdateUserInfoCommand command);
}
