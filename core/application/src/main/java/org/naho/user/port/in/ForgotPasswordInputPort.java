package org.naho.user.port.in;

import org.naho.user.command.ForgotPasswordCommand;

public interface ForgotPasswordInputPort {
    void forgotPassword(ForgotPasswordCommand command);
}
