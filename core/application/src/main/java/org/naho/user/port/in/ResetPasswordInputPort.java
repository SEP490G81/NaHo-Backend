package org.naho.user.port.in;

import org.naho.user.command.ResetPasswordCommand;

public interface ResetPasswordInputPort {
    void resetPassword(ResetPasswordCommand command);
}
