package org.naho.user.usecase;

import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.EmailPort;
import org.naho.user.command.ChangePasswordCommand;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.model.User;
import org.naho.user.port.in.ChangePasswordInputPort;
import org.naho.user.port.out.EncoderPort;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.valueobject.Password;

import java.util.Optional;

public class ChangePasswordUseCase implements ChangePasswordInputPort {

    private final UserRepositoryPort userRepository;
    private final EncoderPort encoderPort;
    private final EmailPort emailPort;

    public ChangePasswordUseCase(
            UserRepositoryPort userRepository,
            EncoderPort encoderPort,
            EmailPort emailPort
    ) {
        this.userRepository = userRepository;
        this.encoderPort = encoderPort;
        this.emailPort = emailPort;
    }

    @Override
    public void changePassword(ChangePasswordCommand command) {
        if (!command.newPassword().equals(command.confirmPassword())) {
            throw new ApplicationException(
                    UserErrorCode.USER_PASSWORD_NOT_MATCH,
                    UserDetailMessageKey.USER_PASSWORD_NOT_MATCH_DETAIL
            );
        }

        Optional<User> userOpt = userRepository.findById(command.userId());
        if (userOpt.isEmpty()) {
            throw new ApplicationException(
                    UserErrorCode.USER_NOT_FOUND,
                    UserDetailMessageKey.USER_ID_NOT_FOUND,
                    command.userId()
            );
        }

        User user = userOpt.get();

        if (user.getHashPassword() == null || user.getHashPassword().isBlank()) {
            // Should not happen for normally logged in users unless they only have social login
            throw new ApplicationException(
                    UserErrorCode.USER_SOCIAL_LOGIN_CANNOT_RESET_PASSWORD,
                    UserDetailMessageKey.USER_SOCIAL_LOGIN_CANNOT_RESET_PASSWORD
            );
        }

        if (!encoderPort.matches(command.oldPassword(), user.getHashPassword())) {
            throw new ApplicationException(
                    UserErrorCode.USER_OLD_PASSWORD_NOT_MATCH,
                    UserDetailMessageKey.USER_OLD_PASSWORD_NOT_MATCH_DETAIL
            );
        }

        if (encoderPort.matches(command.newPassword(), user.getHashPassword())) {
            throw new ApplicationException(
                    UserErrorCode.USER_PASSWORD_SAME_AS_OLD,
                    UserDetailMessageKey.USER_PASSWORD_SAME_AS_OLD_DETAIL
            );
        }

        Password newPassword = Password.of(command.newPassword());
        String encodedPassword = encoderPort.hashPassword(newPassword.getValue());
        user.setHashPassword(encodedPassword);

        userRepository.save(user);

        if (user.getEmail() != null) {
            emailPort.sendPasswordChangedEmail(user.getEmail().getValue(), user.getFullName());
        }
    }
}
