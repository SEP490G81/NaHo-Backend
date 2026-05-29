package org.naho.user.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.user.mapper.UserEntityMapper;
import org.naho.user.model.User;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.repository.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import org.naho.user.entity.UserEntity;
import org.naho.user.type.JLPTLevel;
import org.naho.user.type.RoleName;
import org.naho.user.type.UserStatus;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;
    private final UserEntityMapper userEntityMapper;

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
    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        return userJpaRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .map(userEntityMapper::entityToDomain);
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

    @Override
    public void save(User user) {
        UserEntity entity = userJpaRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + user.getId()));
        entity.setStatus(user.getStatus());
        userJpaRepository.save(entity);
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
}
