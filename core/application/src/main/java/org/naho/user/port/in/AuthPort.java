package org.naho.user.port.in;

import org.naho.user.command.CredentialsLoginCommand;
import org.naho.user.command.LogoutCommand;
import org.naho.user.result.LoginResult;

public interface AuthPort {
    LoginResult credentialsLogin(CredentialsLoginCommand command);

    void logout(LogoutCommand command);
}
