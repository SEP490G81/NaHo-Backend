package org.naho.user.port.in;

import org.naho.user.type.UserStatus;

public interface UpdateUserInputPort {
    UserStatus updateStatus(Long id);
}
