package org.naho.config.application;

import org.naho.file.port.in.AsyncCrudFileInputPort;
import org.naho.file.port.in.CrudFileInputPort;
import org.naho.file.port.in.DeleteFileInputPort;
import org.naho.file.port.in.UploadFileInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.file.port.out.FileValidatorPort;
import org.naho.learning.port.in.CrudUserLearningProgressInputPort;
import org.naho.shared.port.out.EmailPort;
import org.naho.shared.port.out.TransactionPort;
import org.naho.user.mapper.AuthProviderResultMapper;
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
    public AuthProviderResultMapper authProviderResultMapper() {
        return new AuthProviderResultMapper();
    }

    @Bean
    public UserResultMapper userResultMapper(
            CrudRoleInputPort crudRoleInputPort,
            CrudAuthProviderInputPort crudAuthProviderInputPort,
            CrudFileInputPort crudFileInputPort) {
        return new UserResultMapper(
                crudRoleInputPort,
                crudAuthProviderInputPort,
                crudFileInputPort);
    }

    @Bean
    public CrudUserInputPort crudUserInputPort(
            UserRepositoryPort userRepositoryPort,
            FileValidatorPort fileValidatorPort,
            CrudFileInputPort crudFileInputPort,
            UserResultMapper userResultMapper,
            RoleRepositoryPort roleRepositoryPort,
            FileRepositoryPort fileRepositoryPort,
            FileStorageServicePort fileStorageServicePort,
            DeleteFileInputPort deleteFileInputPort,
            UploadFileInputPort uploadFileInputPort,
            TransactionPort transactionPort,
            AsyncCrudFileInputPort asyncCrudFileInputPort
    ) {
        return new CrudUserUseCase(
                userRepositoryPort,
                fileValidatorPort,
                crudFileInputPort,
                userResultMapper,
                roleRepositoryPort,
                fileRepositoryPort,
                fileStorageServicePort,
                deleteFileInputPort,
                uploadFileInputPort,
                transactionPort,
                asyncCrudFileInputPort
        );
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
    public CrudAuthProviderInputPort crudAuthProviderInputPort(
            AuthProviderRepositoryPort authProviderRepositoryPort,
            AuthProviderResultMapper authProviderResultMapper) {
        return new CrudAuthProviderUseCase(
                authProviderRepositoryPort,
                authProviderResultMapper);
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

    @Bean
    public VerifyForgotPasswordOtpInputPort verifyForgotPasswordOtpInputPort(
            UserRepositoryPort userRepositoryPort,
            PasswordResetOtpPort passwordResetOtpPort) {
        return new VerifyForgotPasswordOtpUseCase(
                userRepositoryPort,
                passwordResetOtpPort);
    }

    @Bean
    public ChangePasswordInputPort changePasswordInputPort(
            UserRepositoryPort userRepositoryPort,
            EncoderPort encoderPort,
            EmailPort emailPort) {
        return new ChangePasswordUseCase(
                userRepositoryPort,
                encoderPort,
                emailPort);
    }
}
