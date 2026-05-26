package org.naho.config;

import org.naho.file.mapper.FileResultMapper;
import org.naho.user.mapper.UserResultMapper;
import org.naho.user.port.in.AuthPort;
import org.naho.user.port.out.JwtServicePort;
import org.naho.user.port.out.PasswordEncoderPort;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.usecase.AuthUseCase;
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
}
