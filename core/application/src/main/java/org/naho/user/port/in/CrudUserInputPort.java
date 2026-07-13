package org.naho.user.port.in;

import org.naho.user.result.UserResult;

public interface CrudUserInputPort {
    UserResult findUserById(Long id);
}
