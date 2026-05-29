package org.naho.config;

import org.naho.file.port.out.FileRepositoryPort;
import org.naho.shared.port.out.TransactionPort;
import org.naho.user.mapper.UserResultMapper;
import org.naho.user.port.in.AuthPort;
import org.naho.user.port.in.GetRoleInputPort;
import org.naho.user.port.in.GetUserInputPort;
import org.naho.user.port.in.UpdateUserInputPort;
import org.naho.user.port.out.*;
import org.naho.user.usecase.AuthUseCase;
import org.naho.user.usecase.GetRoleUseCase;
import org.naho.user.usecase.GetUserUseCase;
import org.naho.user.usecase.UpdateUserUseCase;
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
            UserRepositoryPort userRepositoryPort
    ) {
        return new UpdateUserUseCase(userRepositoryPort);
    }

    @Bean
    public GetRoleInputPort getRoleInputPort(
            RoleRepositoryPort roleRepositoryPort
    ) {
        return new GetRoleUseCase(roleRepositoryPort);
    }
}
