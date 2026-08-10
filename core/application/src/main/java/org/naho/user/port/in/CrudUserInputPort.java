package org.naho.user.port.in;

import org.naho.pagination.PageData;
import org.naho.user.command.UpdateUserAvatarCommand;
import org.naho.user.command.UpdateUserInfoCommand;
import org.naho.user.command.UserQueryCommand;
import org.naho.user.result.UserResult;

public interface CrudUserInputPort {
    UserResult findUserById(Long id);

    UserResult updateUserInfo(UpdateUserInfoCommand command);

    UserResult updateUserAvatar(UpdateUserAvatarCommand command);

    PageData<UserResult> findAllUsers(UserQueryCommand command);
}
