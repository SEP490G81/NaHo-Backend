package org.naho.config.application;

import org.naho.file.port.out.FileRepositoryPort;
import org.naho.shared.port.out.TransactionPort;
import org.naho.user.mapper.UserResultMapper;
import org.naho.user.port.in.*;
import org.naho.user.port.out.*;
import org.naho.user.usecase.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfig {
    @Bean
    public UserResultMapper userResultMapper() {
        return new UserResultMapper();
    }

    @Bean
    public AuthPort authPort(
            UserRepositoryPort userRepositoryPort,
            EncoderPort encoderPort,
            TokenServicePort tokenServicePort,
            UserResultMapper userResultMapper,
            UserSessionRepositoryPort userSessionRepositoryPort,
            RoleRepositoryPort roleRepositoryPort,
            FileRepositoryPort fileRepositoryPort,
            TransactionPort transactionPort

    ) {
        return new AuthUseCase(
                userRepositoryPort,
                encoderPort,
                tokenServicePort,
                userResultMapper,
                userSessionRepositoryPort,
                roleRepositoryPort,
                fileRepositoryPort,
                transactionPort
        );
    }

    @Bean
    public RegisterInputPort registerInputPort(
            UserRepositoryPort userRepository,
            PasswordEncoderPort passwordEncoder,
            RoleRepositoryPort roleRepository
    ) {
        return new RegisterUseCase(userRepository, passwordEncoder, roleRepository);
    }

    @Bean
    public GetUserInputPort getUserInputPort(
            UserRepositoryPort userRepositoryPort,
            RoleRepositoryPort roleRepositoryPort,
            FileRepositoryPort fileRepositoryPort,
            UserResultMapper userResultMapper
    ) {
        return new GetUserUseCase(userRepositoryPort, roleRepositoryPort, fileRepositoryPort, userResultMapper);
    }

    @Bean
    public UpdateUserInputPort updateUserInputPort(
            UserRepositoryPort userRepositoryPort,
            UserResultMapper userResultMapper,
            RoleRepositoryPort roleRepositoryPort,
            FileRepositoryPort fileRepositoryPort
    ) {
        return new UpdateUserUseCase(
                userRepositoryPort,
                userResultMapper,
                roleRepositoryPort,
                fileRepositoryPort
        );
    }

    @Bean
    public GetRoleInputPort getRoleInputPort(
            RoleRepositoryPort roleRepositoryPort
    ) {
        return new GetRoleUseCase(roleRepositoryPort);
    }
}
