package org.naho.config.application;

import org.naho.shared.port.out.TransactionPort;
import org.naho.user.helper.AuthUseCaseHelper;
import org.naho.user.mapper.UserResultMapper;
import org.naho.user.port.in.*;
import org.naho.user.port.out.*;
import org.naho.user.usecase.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua_parser.Parser;

@Configuration
public class UserConfig {
    @Bean
    public Parser parser() {
        return new Parser();
    }

    @Bean
    public UserResultMapper userResultMapper() {
        return new UserResultMapper();
    }

    @Bean
    public AuthUseCaseHelper authUseCaseHelper(
            RoleRepositoryPort roleRepositoryPort,
            TokenServicePort tokenServicePort,
            UserResultMapper userResultMapper,
            UserRepositoryPort userRepositoryPort
    ) {
        return new AuthUseCaseHelper(
                roleRepositoryPort,
                tokenServicePort,
                userResultMapper,
                userRepositoryPort
        );
    }

    @Bean
    public AuthInputPort authPort(
            UserRepositoryPort userRepositoryPort,
            EncoderPort encoderPort,
            TokenServicePort tokenServicePort,
            UserResultMapper userResultMapper,
            UserSessionRepositoryPort userSessionRepositoryPort,
            RoleRepositoryPort roleRepositoryPort,
            TransactionPort transactionPort,
            AuthUseCaseHelper authUseCaseHelper,
            UserSessionServicePort userSessionServicePort
    ) {
        return new AuthUseCase(
                userRepositoryPort,
                encoderPort,
                tokenServicePort,
                userResultMapper,
                userSessionRepositoryPort,
                roleRepositoryPort,
                transactionPort,
                authUseCaseHelper,
                userSessionServicePort
        );
    }

    @Bean
    public RegisterInputPort registerInputPort(
            UserRepositoryPort userRepository,
            EncoderPort encoderPort,
            RoleRepositoryPort roleRepository
    ) {
        return new RegisterUseCase(userRepository, encoderPort, roleRepository);
    }

    @Bean
    public GetUserInputPort getUserInputPort(
            UserRepositoryPort userRepositoryPort,
            RoleRepositoryPort roleRepositoryPort,
            UserResultMapper userResultMapper
    ) {
        return new GetUserUseCase(userRepositoryPort, roleRepositoryPort, userResultMapper);
    }

    @Bean
    public UpdateUserInputPort updateUserInputPort(
            UserRepositoryPort userRepositoryPort,
            UserResultMapper userResultMapper,
            RoleRepositoryPort roleRepositoryPort
    ) {
        return new UpdateUserUseCase(
                userRepositoryPort,
                userResultMapper,
                roleRepositoryPort
        );
    }

    @Bean
    public GetRoleInputPort getRoleInputPort(
            RoleRepositoryPort roleRepositoryPort
    ) {
        return new GetRoleUseCase(roleRepositoryPort);
    }
}
