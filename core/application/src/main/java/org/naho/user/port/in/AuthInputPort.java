package org.naho.user.port.in;

import org.naho.user.command.CredentialsLoginCommand;
import org.naho.user.command.GoogleLoginCommand;
import org.naho.user.command.LogoutCommand;
import org.naho.user.result.LoginResult;
import org.naho.user.result.UserResult;

public interface AuthInputPort {
    LoginResult credentialsLogin(CredentialsLoginCommand command);

    LoginResult googleLogin(GoogleLoginCommand command);

    void logout(LogoutCommand command);

    LoginResult rotateToken(String refreshToken);

    UserResult findUserById(Long userId);

    void logoutAllSessions(Long userId);
}
