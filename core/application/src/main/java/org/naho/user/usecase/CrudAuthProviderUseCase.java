package org.naho.user.usecase;

import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.mapper.AuthProviderResultMapper;
import org.naho.user.model.AuthProvider;
import org.naho.user.port.in.CrudAuthProviderInputPort;
import org.naho.user.port.out.AuthProviderRepositoryPort;
import org.naho.user.result.AuthProviderResult;

import java.util.List;

public class CrudAuthProviderUseCase implements CrudAuthProviderInputPort {

    private final AuthProviderRepositoryPort authProviderRepositoryPort;
    private final AuthProviderResultMapper authProviderResultMapper;

    public CrudAuthProviderUseCase(
            AuthProviderRepositoryPort authProviderRepositoryPort,
            AuthProviderResultMapper authProviderResultMapper
    ) {
        this.authProviderRepositoryPort = authProviderRepositoryPort;
        this.authProviderResultMapper = authProviderResultMapper;
    }

    @Override
    public List<AuthProviderResult> findAllByUser_Id(Long userId) {
        if (userId == null) {
            throw new ApplicationException(
                    UserErrorCode.USER_NOT_FOUND,
                    UserDetailMessageKey.USER_ID_NULL
            );
        }

        List<AuthProvider> authProviders = authProviderRepositoryPort.findAllByUser_Id(userId);
        return authProviders.stream()
                .map(authProviderResultMapper::domainToResult)
                .toList();
    }
}
