package org.naho.config.application;

import org.naho.file.port.in.CrudFileInputPort;
import org.naho.shared.port.out.TransactionPort;
import org.naho.user.mapper.OAuthProviderResultMapper;
import org.naho.user.mapper.RoleResultMapper;
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
        public RoleResultMapper roleResultMapper() {
                return new RoleResultMapper();
        }

        @Bean
        public OAuthProviderResultMapper oAuthProviderResultMapper() {
                return new OAuthProviderResultMapper();
        }

        @Bean
        public UserResultMapper userResultMapper(
                        CrudRoleInputPort crudRoleInputPort,
                        CrudOAuthProviderInputPort crudOAuthProviderInputPort,
                        CrudFileInputPort crudFileInputPort) {
                return new UserResultMapper(
                                crudRoleInputPort,
                                crudOAuthProviderInputPort,
                                crudFileInputPort);
        }

        @Bean
        public CrudUserInputPort crudUserInputPort(
                        UserRepositoryPort userRepositoryPort,
                        UserResultMapper userResultMapper,
                        RoleRepositoryPort roleRepositoryPort) {
                return new CrudUserUseCase(
                                userRepositoryPort,
                                userResultMapper,
                                roleRepositoryPort);
        }

        @Bean
        public CrudRoleInputPort crudRoleInputPort(
                        RoleRepositoryPort roleRepositoryPort,
                        RoleResultMapper roleResultMapper) {
                return new CrudRoleUseCase(
                                roleRepositoryPort,
                                roleResultMapper);
        }

        @Bean
        public CrudOAuthProviderInputPort crudOAuthProviderInputPort(
                        OAuthProviderRepositoryPort oAuthProviderRepositoryPort,
                        OAuthProviderResultMapper oAuthProviderResultMapper) {
                return new CrudOAuthProviderUseCase(
                                oAuthProviderRepositoryPort,
                                oAuthProviderResultMapper);
        }

        @Bean
        public AuthInputPort authPort(
                        UserRepositoryPort userRepositoryPort,
                        EncoderPort encoderPort,
                        TokenServicePort tokenServicePort,
                        UserSessionRepositoryPort userSessionRepositoryPort,
                        RoleRepositoryPort roleRepositoryPort,
                        TransactionPort transactionPort,
                        UserSessionServicePort userSessionServicePort,
                        UserSessionEventPublisherPort userSessionEventPublisherPort) {
                return new AuthUseCase(
                                userRepositoryPort,
                                encoderPort,
                                tokenServicePort,
                                userSessionRepositoryPort,
                                roleRepositoryPort,
                                transactionPort,
                                userSessionServicePort,
                                userSessionEventPublisherPort);
        }

        @Bean
        public RegisterInputPort registerInputPort(
                        UserRepositoryPort userRepository,
                        EncoderPort encoderPort,
                        RoleRepositoryPort roleRepository) {
                return new RegisterUseCase(userRepository, encoderPort, roleRepository);
        }

        @Bean
        public GetUserInputPort getUserInputPort(
                        UserRepositoryPort userRepositoryPort,
                        UserResultMapper userResultMapper) {
                return new GetUserUseCase(userRepositoryPort, userResultMapper);
        }

        @Bean
        public UpdateUserInputPort updateUserInputPort(
                        UserRepositoryPort userRepositoryPort,
                        UserResultMapper userResultMapper) {
                return new UpdateUserUseCase(
                                userRepositoryPort,
                                userResultMapper);
        }
}
