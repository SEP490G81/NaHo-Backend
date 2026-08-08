package org.naho.user.port.in;

import org.naho.user.result.AuthProviderResult;

import java.util.List;

public interface CrudAuthProviderInputPort {
    List<AuthProviderResult> findAllByUser_Id(Long userId);
}
