package org.naho.config.application;

import org.naho.file.port.in.CrudFileInputPort;
import org.naho.learning.port.in.CrudUserLearningProgressInputPort;
import org.naho.shared.port.out.EmailPort;
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
            UserSessionEventPublisherPort userSessionEventPublisherPort,
            CrudUserLearningProgressInputPort crudUserLearningProgressInputPort,
            OtpPort otpPort,
            EmailPort emailPort
    ) {
        return new AuthUseCase(
                userRepositoryPort,
                encoderPort,
                tokenServicePort,
                userSessionRepositoryPort,
                roleRepositoryPort,
                transactionPort,
                userSessionServicePort,
                userSessionEventPublisherPort,
                crudUserLearningProgressInputPort,
                otpPort,
                emailPort
        );
    }

    @Bean
    public RegisterInputPort registerInputPort(
            UserRepositoryPort userRepository,
            EncoderPort encoderPort,
            RoleRepositoryPort roleRepository,
            CrudUserLearningProgressInputPort crudUserLearningProgressInputPort,
            OtpPort otpPort,
            EmailPort emailPort
    ) {
        return new RegisterUseCase(
                userRepository,
                encoderPort,
                roleRepository,
                crudUserLearningProgressInputPort,
                otpPort,
                emailPort
        );
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

    @Bean
    public VerifyEmailInputPort verifyEmailInputPort(
            UserRepositoryPort userRepositoryPort,
            OtpPort otpPort,
            TransactionPort transactionPort,
            TokenServicePort tokenServicePort,
            EncoderPort encoderPort,
            UserSessionRepositoryPort userSessionRepositoryPort,
            UserSessionServicePort userSessionServicePort,
            UserSessionEventPublisherPort userSessionEventPublisherPort) {
        return new VerifyEmailUseCase(
                userRepositoryPort,
                otpPort,
                transactionPort,
                tokenServicePort,
                encoderPort,
                userSessionRepositoryPort,
                userSessionServicePort,
                userSessionEventPublisherPort);
    }

    @Bean
    public ResendOtpInputPort resendOtpInputPort(
            UserRepositoryPort userRepositoryPort,
            OtpPort otpPort,
            EmailPort emailPort) {
        return new ResendOtpUseCase(
                userRepositoryPort,
                otpPort,
                emailPort);
    }

    @Bean
    public ForgotPasswordInputPort forgotPasswordInputPort(
            UserRepositoryPort userRepositoryPort,
            PasswordResetOtpPort passwordResetOtpPort,
            EmailPort emailPort) {
        return new ForgotPasswordUseCase(
                userRepositoryPort,
                passwordResetOtpPort,
                emailPort);
    }

    @Bean
    public ResetPasswordInputPort resetPasswordInputPort(
            UserRepositoryPort userRepositoryPort,
            PasswordResetOtpPort passwordResetOtpPort,
            EncoderPort encoderPort) {
        return new ResetPasswordUseCase(
                userRepositoryPort,
                passwordResetOtpPort,
                encoderPort);
    }
}
