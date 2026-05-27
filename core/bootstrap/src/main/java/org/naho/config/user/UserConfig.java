package org.naho.config.user;

import org.naho.user.port.in.RegisterInputPort;
import org.naho.user.port.out.PasswordEncoderPort;
import org.naho.user.port.repository.RoleRepository;
import org.naho.user.port.repository.UserRepository;
import org.naho.user.usecase.RegisterUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfig {

    @Bean
    public RegisterInputPort registerInputPort(UserRepository userRepository, PasswordEncoderPort passwordEncoder, RoleRepository roleRepository) {
        return new RegisterUseCase(userRepository, passwordEncoder, roleRepository);
    }
}
