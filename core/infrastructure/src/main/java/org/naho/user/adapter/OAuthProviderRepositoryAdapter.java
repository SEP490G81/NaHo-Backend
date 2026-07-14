package org.naho.user.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.user.entity.OAuthProviderEntity;
import org.naho.user.mapper.OAuthProviderEntityMapper;
import org.naho.user.model.OAuthProvider;
import org.naho.user.port.out.OAuthProviderRepositoryPort;
import org.naho.user.repository.OAuthProviderJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OAuthProviderRepositoryAdapter implements OAuthProviderRepositoryPort {

    private final OAuthProviderJpaRepository oAuthProviderJpaRepository;
    private final OAuthProviderEntityMapper oAuthProviderEntityMapper;

    @Override
    public List<OAuthProvider> findAllByUser_Id(Long userId) {
        List<OAuthProviderEntity> oAuthProviderEntityList =
                oAuthProviderJpaRepository.findAllByUser_Id(userId);
        
        return oAuthProviderEntityList.stream()
                .map(oAuthProviderEntityMapper::entityToDomain)
                .toList();
    }
}
