package org.naho.user.port.in;

import org.naho.user.command.ChangePasswordCommand;

public interface ChangePasswordInputPort {
    void changePassword(ChangePasswordCommand command);
}
