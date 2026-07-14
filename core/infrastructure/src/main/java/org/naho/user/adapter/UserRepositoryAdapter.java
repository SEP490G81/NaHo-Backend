package org.naho.user.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.point.entity.PointSummaryEntity;
import org.naho.user.entity.OAuthProviderEntity;
import org.naho.user.entity.UserEntity;
import org.naho.user.mapper.OAuthProviderEntityMapper;
import org.naho.user.mapper.UserEntityMapper;
import org.naho.user.model.OAuthProvider;
import org.naho.user.model.User;
import org.naho.user.mybatis.UserQueryMapper;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.repository.UserJpaRepository;
import org.naho.user.type.JLPTLevel;
import org.naho.user.type.OAuthProviderName;
import org.naho.user.type.RoleName;
import org.naho.user.type.UserStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;
    private final UserEntityMapper userEntityMapper;
    private final UserQueryMapper userQueryMapper;
    private final OAuthProviderEntityMapper oAuthProviderEntityMapper;

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username)
                .map(userEntityMapper::entityToDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .map(userEntityMapper::entityToDomain);
    }

    @Override
    public Optional<User> findByProviderUserIdAndProviderName(String providerId, OAuthProviderName providerName) {
        return userQueryMapper.findByProviderUserIdAndProviderName(providerId, providerName)
                .map(userEntityMapper::entityToDomain);
    }

    @Override
    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        return userJpaRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .map(userEntityMapper::entityToDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userJpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public User save(User user, OAuthProvider oAuthProvider) {
        UserEntity userEntity = userEntityMapper.domainToEntity(user);
        
        if (oAuthProvider != null) {
            OAuthProviderEntity oAuthProviderEntity =
                    oAuthProviderEntityMapper.domainToEntity(oAuthProvider);
            oAuthProviderEntity.setUser(userEntity);
            userEntity.getOAuthProviders().add(oAuthProviderEntity);
        }

        UserEntity savedEntity = userJpaRepository.save(userEntity);
        return userEntityMapper.entityToDomain(savedEntity);
    }

    @Override
    public User createNew(User user, OAuthProvider oAuthProvider) {
        UserEntity userEntity = userEntityMapper.domainToEntity(user);
        // default point summary
        userEntity.setPointSummary(
                PointSummaryEntity.builder()
                        .totalPoint(0.0)
                        .build()
        );

        if (oAuthProvider != null) {
            OAuthProviderEntity oAuthProviderEntity =
                    oAuthProviderEntityMapper.domainToEntity(oAuthProvider);
            oAuthProviderEntity.setUser(userEntity);
            userEntity.getOAuthProviders().add(oAuthProviderEntity);
        }

        UserEntity savedEntity = userJpaRepository.save(userEntity);
        return userEntityMapper.entityToDomain(savedEntity);
    }

    @Override
    public List<User> findByFilters(String userNameOrMail, String role, String status, String jlptLevel) {
        RoleName roleEnum = null;
        if (role != null && !role.trim().isEmpty()) {
            try {
                roleEnum = RoleName.valueOf(role.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                return List.of();
            }
        }

        UserStatus statusEnum = null;
        if (status != null && !status.trim().isEmpty()) {
            try {
                statusEnum = UserStatus.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                return List.of();
            }
        }

        JLPTLevel jlptLevelEnum = null;
        if (jlptLevel != null && !jlptLevel.trim().isEmpty()) {
            try {
                jlptLevelEnum = JLPTLevel.valueOf(jlptLevel.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                return List.of();
            }
        }

        String formattedNameOrMail = null;
        if (userNameOrMail != null && !userNameOrMail.trim().isEmpty()) {
            formattedNameOrMail = "%" + userNameOrMail.trim().toLowerCase() + "%";
        }

        return userJpaRepository.findByFilters(formattedNameOrMail, roleEnum, statusEnum, jlptLevelEnum).stream()
                .map(userEntityMapper::entityToDomain)
                .toList();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id)
                .map(userEntityMapper::entityToDomain);
    }

    @Override
    public List<User> getListUser() {
        return userJpaRepository.findAll().stream()
                .map(userEntityMapper::entityToDomain)
                .toList();
    }
}
