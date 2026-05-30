package org.naho.user.port.in;

import org.naho.user.result.UserResult;

public interface UpdateUserInputPort {
    UserResult updateStatus(Long id, String status);
}
