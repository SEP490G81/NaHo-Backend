package org.naho.user.usecase;

import org.naho.shared.exception.ApplicationException;
import org.naho.user.command.CredentialsLoginCommand;
import org.naho.user.constant.UserApplicationMessageKey;
import org.naho.user.exception.UserApplicationErrorCode;
import org.naho.user.model.User;
import org.naho.user.port.in.AuthenticationInputPort;
import org.naho.user.port.repository.UserRepository;
import org.naho.user.result.LoginResult;

public class AuthenticationUseCase implements AuthenticationInputPort {
    private final UserRepository userRepository;

    public AuthenticationUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public LoginResult credentialsLogin(CredentialsLoginCommand command) {
        User user = userRepository.findByUsername(command.username())
                .orElseThrow(() -> new ApplicationException(
                        UserApplicationErrorCode.USER_NOT_FOUND,
                        UserApplicationMessageKey.USER_WRONG_USERNAME_OR_PASSWORD,
                        command.username()
                ));

        return null;
    }

}
