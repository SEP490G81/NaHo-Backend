package org.naho.user.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.user.entity.AuthProviderEntity;
import org.naho.user.mapper.AuthProviderEntityMapper;
import org.naho.user.model.AuthProvider;
import org.naho.user.port.out.AuthProviderRepositoryPort;
import org.naho.user.repository.AuthProviderJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthProviderRepositoryAdapter implements AuthProviderRepositoryPort {

    private final AuthProviderJpaRepository authProviderJpaRepository;
    private final AuthProviderEntityMapper authProviderEntityMapper;

    @Override
    public List<AuthProvider> findAllByUser_Id(Long userId) {
        List<AuthProviderEntity> authProviderEntityList =
                authProviderJpaRepository.findAllByUser_Id(userId);

        return authProviderEntityList.stream()
                .map(authProviderEntityMapper::entityToDomain)
                .toList();
    }
}
