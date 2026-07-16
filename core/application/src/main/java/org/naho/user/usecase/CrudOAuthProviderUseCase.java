package org.naho.user.usecase;

import org.naho.user.mapper.OAuthProviderResultMapper;
import org.naho.user.model.OAuthProvider;
import org.naho.user.port.in.CrudOAuthProviderInputPort;
import org.naho.user.port.out.OAuthProviderRepositoryPort;
import org.naho.user.result.OAuthProviderResult;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.user.exception.UserErrorCode;

import java.util.List;

public class CrudOAuthProviderUseCase implements CrudOAuthProviderInputPort {

    private final OAuthProviderRepositoryPort oAuthProviderRepositoryPort;
    private final OAuthProviderResultMapper oAuthProviderResultMapper;

    public CrudOAuthProviderUseCase(
            OAuthProviderRepositoryPort oAuthProviderRepositoryPort,
            OAuthProviderResultMapper oAuthProviderResultMapper
    ) {
        this.oAuthProviderRepositoryPort = oAuthProviderRepositoryPort;
        this.oAuthProviderResultMapper = oAuthProviderResultMapper;
    }

    @Override
    public List<OAuthProviderResult> findAllByUser_Id(Long userId) {
        if (userId == null) {
            throw new ApplicationException(
                    UserErrorCode.USER_NOT_FOUND,
                    UserDetailMessageKey.USER_ID_NULL
            );
        }

        List<OAuthProvider> oAuthProviders = oAuthProviderRepositoryPort.findAllByUser_Id(userId);
        return oAuthProviders.stream()
                .map(oAuthProviderResultMapper::domainToResult)
                .toList();
    }
}
