package org.naho.config;

import org.naho.file.mapper.FileResultMapper;
import org.naho.user.mapper.UserResultMapper;
import org.naho.user.port.in.AuthPort;
import org.naho.user.port.in.GetRoleInputPort;
import org.naho.user.port.in.GetUserInputPort;
import org.naho.user.port.in.UpdateUserInputPort;
import org.naho.user.port.out.JwtServicePort;
import org.naho.user.port.out.PasswordEncoderPort;
import org.naho.user.port.out.RoleRepositoryPort;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.usecase.AuthUseCase;
import org.naho.user.usecase.GetRoleUseCase;
import org.naho.user.usecase.GetUserUseCase;
import org.naho.user.usecase.UpdateUserUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfig {
    @Bean
    public UserResultMapper userResultMapper(FileResultMapper fileResultMapper) {
        return new UserResultMapper(fileResultMapper);
    }

    @Bean
    public AuthPort authPort(UserRepositoryPort userRepositoryPort,
                             PasswordEncoderPort passwordEncoderPort,
                             JwtServicePort jwtServicePort,
                             UserResultMapper userResultMapper
    ) {
        return new AuthUseCase(userRepositoryPort, passwordEncoderPort, jwtServicePort, userResultMapper);
    }

    @Bean
    public GetUserInputPort getUserInputPort(
            UserRepositoryPort userRepositoryPort,
            UserResultMapper userResultMapper
    ){
        return new GetUserUseCase(userRepositoryPort,userResultMapper);
    }

    @Bean
    public UpdateUserInputPort updateUserInputPort(
            UserRepositoryPort userRepositoryPort
    ){
        return new UpdateUserUseCase(userRepositoryPort);
    }

    @Bean
    public GetRoleInputPort getRoleInputPort(
            RoleRepositoryPort roleRepositoryPort
    ) {
        return new GetRoleUseCase(roleRepositoryPort);
    }
}
