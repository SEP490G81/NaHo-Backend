package org.naho.user.port.in;

import org.naho.file.result.FileResult;
import org.naho.file.result.StoredFile;
import org.naho.user.command.UpdateUserInfoCommand;
import org.naho.user.result.UserResult;

public interface CrudUserInputPort {
    UserResult findUserById(Long id);

    UserResult updateUserInfo(UpdateUserInfoCommand command);

    FileResult updateUserAvatar(Long id, StoredFile storedFile);
}
