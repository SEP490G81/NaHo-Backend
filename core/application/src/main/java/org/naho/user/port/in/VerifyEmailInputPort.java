package org.naho.user.port.in;

import org.naho.user.command.VerifyEmailCommand;
import org.naho.user.result.LoginResult;

public interface VerifyEmailInputPort {
    LoginResult verifyEmail(VerifyEmailCommand command);
}
