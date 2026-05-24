package org.naho.user.port.in;

import org.naho.user.command.CredentialsLoginCommand;
import org.naho.user.result.LoginResult;

public interface AuthenticationInputPort {
    LoginResult credentialsLogin(CredentialsLoginCommand command);
}
