package org.naho.user.usecase;

import org.naho.i18n.MessageService;
import org.naho.shared.exception.ApplicationException;
import org.naho.user.command.RegisterCommand;
import org.naho.user.constant.UserApplicationMessageKey;
import org.naho.user.exception.UserApplicationErrorCode;
import org.naho.user.model.Role;
import org.naho.user.model.User;
import org.naho.user.port.in.RegisterInputPort;
import org.naho.user.port.out.PasswordEncoderPort;
import org.naho.user.port.out.RoleRepositoryPort;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.result.RegisterResult;
import org.naho.user.type.RoleName;

import java.util.List;

public class RegisterUseCase implements RegisterInputPort {

    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final MessageService messageService;

    public RegisterUseCase(
            UserRepositoryPort userRepository,
            PasswordEncoderPort passwordEncoder,
            RoleRepositoryPort roleRepository,
            MessageService messageService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.messageService = messageService;
    }

    @Override
    public RegisterResult register(RegisterCommand command) {
        // Check already user
        if (userRepository.existsByUsername(command.username())) {
            throw new ApplicationException(
                    UserApplicationErrorCode.USER_ALREADY_EXISTS,
                    UserApplicationMessageKey.USER_USERNAME_ALREADY_EXISTS,
                    command.username()
            );
        }
        if (userRepository.existsByEmail(command.email())) {
            throw new ApplicationException(
                    UserApplicationErrorCode.USER_ALREADY_EXISTS,
                    UserApplicationMessageKey.USER_EMAIL_ALREADY_EXISTS,
                    command.email());
        }

        // Encode pass
        String encodedPassword = passwordEncoder.encode(command.password());

        // Assign StudentRole
        Role defaultRole = roleRepository.findByName(RoleName.STUDENT).orElseThrow(() -> new ApplicationException(
                UserApplicationErrorCode.USER_ROLE_NOT_VALID,
                UserApplicationMessageKey.USER_ROLE_NOT_FOUND));

        User newUser = User.registerNewUser(
                command.username(),
                encodedPassword,
                command.email(),
                List.of(defaultRole.getId()));

        User savedUser = userRepository.save(newUser);

        return new RegisterResult(
                savedUser.getId() != null ? savedUser.getId().toString() : "",
                savedUser.getUsername().getValue(),
                savedUser.getEmail().getValue());
    }
}
