package org.naho.user.usecase;

import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.learning.port.in.CrudUserLearningProgressInputPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.EmailPort;
import org.naho.user.command.RegisterCommand;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.model.Role;
import org.naho.user.model.User;
import org.naho.user.port.in.RegisterInputPort;
import org.naho.user.port.out.EncoderPort;
import org.naho.user.port.out.OtpPort;
import org.naho.user.port.out.RoleRepositoryPort;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.result.RegisterResult;
import org.naho.user.type.RoleName;

import java.util.List;

public class RegisterUseCase implements RegisterInputPort {

    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;
    private final EncoderPort encoderPort;
    private final CrudUserLearningProgressInputPort crudUserLearningProgressInputPort;
    private final OtpPort otpPort;
    private final EmailPort emailPort;

    public RegisterUseCase(
            UserRepositoryPort userRepository,
            EncoderPort encoderPort,
            RoleRepositoryPort roleRepository,
            CrudUserLearningProgressInputPort crudUserLearningProgressInputPort,
            OtpPort otpPort,
            EmailPort emailPort
    ) {
        this.userRepository = userRepository;
        this.encoderPort = encoderPort;
        this.roleRepository = roleRepository;
        this.crudUserLearningProgressInputPort = crudUserLearningProgressInputPort;
        this.otpPort = otpPort;
        this.emailPort = emailPort;
    }

    @Override
    public RegisterResult register(RegisterCommand command) {
        // Check already user
        if (userRepository.existsByUsername(command.username())) {
            throw new ApplicationException(
                    UserErrorCode.USER_ALREADY_EXISTS,
                    UserDetailMessageKey.USER_USERNAME_ALREADY_EXISTS,
                    command.username()
            );
        }
        if (userRepository.existsByEmail(command.email())) {
            throw new ApplicationException(
                    UserErrorCode.USER_ALREADY_EXISTS,
                    UserDetailMessageKey.USER_EMAIL_ALREADY_EXISTS,
                    command.email());
        }

        // Encode pass
        String encodedPassword = encoderPort.hashPassword(command.password());

        // Assign LEARNERRole
        Role defaultRole = roleRepository.findByName(RoleName.LEARNER)
                .orElseThrow(() -> new ApplicationException(
                        UserErrorCode.USER_ROLE_NOT_VALID,
                        UserDetailMessageKey.USER_ROLE_NOT_FOUND
                ));

        User newUser = User.registerNewUser(
                command.username(),
                encodedPassword,
                command.email(),
                List.of(defaultRole.getId()));

        User savedUser = userRepository.createNew(newUser, null);

        // init user learning progress
        crudUserLearningProgressInputPort.initUserLearningProgress(savedUser.getId());

        // Send OTP
        String email = savedUser.getEmail().getValue();
        String otp = otpPort.generateOtp();
        otpPort.saveOtp(email, otp);
        emailPort.sendOtpEmail(email, otp);

        return new RegisterResult(
                savedUser.getId() != null ? savedUser.getId().toString() : "",
                savedUser.getUsername().getValue(),
                email);
    }
}
