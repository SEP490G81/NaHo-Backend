package org.naho.user.usecase;

import org.naho.shared.exception.ApplicationException;
import org.naho.user.command.RegisterCommand;
import org.naho.user.constant.UserApplicationMessageKey;
import org.naho.user.exception.UserApplicationErrorCode;
import org.naho.user.model.Role;
import org.naho.user.model.User;
import org.naho.user.port.in.RegisterInputPort;
import org.naho.user.port.out.PasswordEncoderPort;
import org.naho.user.port.out.RoleRepository;
import org.naho.user.port.out.UserRepository;
import org.naho.user.result.RegisterResult;
import org.naho.user.type.RoleName;

import java.util.HashSet;
import java.util.Set;

public class RegisterUseCase implements RegisterInputPort {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoderPort passwordEncoder;

    public RegisterUseCase(UserRepository userRepository, PasswordEncoderPort passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public RegisterResult register(RegisterCommand command) {
        // Check already user
        if (userRepository.existsByUsername(command.username())){
            throw new ApplicationException(
                    UserApplicationErrorCode.USER_ALREADY_EXISTS,
                    UserApplicationMessageKey.USER_USERNAME_ALREADY_EXISTS,
                    command.username()
            );
        }
        if (userRepository.existsByEmail(command.email())){
            throw new ApplicationException(
                    UserApplicationErrorCode.USER_ALREADY_EXISTS,
                    UserApplicationMessageKey.USER_EMAIL_ALREADY_EXISTS,
                    command.email()
            );
        }

        // Encode pass
        String encodedPassword = passwordEncoder.encode(command.password());

        // Assign StudentRole
        Set<Role> assignedRole = new HashSet<>();
        Role defaultRole = roleRepository.findByName(RoleName.STUDENT).orElseThrow(()-> new ApplicationException(
                UserApplicationErrorCode.USER_ROLE_NOT_VALID,
                UserApplicationMessageKey.USER_ROLE_NOT_FOUND
        ));

        assignedRole.add(defaultRole);

        User newUser = User.registerNewUser(
                command.username(),
                encodedPassword,
                command.email(),
                assignedRole
        );

        User savedUser = userRepository.save(newUser);

        return new RegisterResult(
                savedUser.getId()!=null ? savedUser.getId().toString() : "",
                savedUser.getUsername().getValue(),
                savedUser.getEmail().getValue()
        );
    }
}
