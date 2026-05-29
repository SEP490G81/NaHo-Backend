package org.naho.config;

import org.naho.file.port.out.FileRepositoryPort;
import org.naho.shared.port.out.TransactionPort;
import org.naho.user.mapper.UserResultMapper;
import org.naho.user.port.in.AuthPort;
import org.naho.user.port.in.RegisterInputPort;
import org.naho.user.port.out.*;
import org.naho.user.usecase.AuthUseCase;
import org.naho.user.usecase.RegisterUseCase;
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
    public RegisterInputPort registerInputPort(UserRepositoryPort userRepository, PasswordEncoderPort passwordEncoder, RoleRepositoryPort roleRepository) {
        return new RegisterUseCase(userRepository, passwordEncoder, roleRepository);
    }
}
