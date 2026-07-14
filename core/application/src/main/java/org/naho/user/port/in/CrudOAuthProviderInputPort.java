package org.naho.user.port.in;

import org.naho.user.result.OAuthProviderResult;

import java.util.List;

public interface CrudOAuthProviderInputPort {
    List<OAuthProviderResult> findAllByUser_Id(Long userId);
}
