package org.naho.user.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.user.entity.UserEntity;
import org.naho.user.model.User;
import org.naho.user.model.Role;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.repository.UserJpaRepository;
import org.naho.user.valueobject.Username;
import org.naho.user.valueobject.Email;
import org.naho.user.valueobject.Dob;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepositoryPortAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public List<User> getListUser() {
        return userJpaRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    private User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        return User.builder()
                .id(entity.getId())
                .username(entity.getUsername() != null ? Username.of(entity.getUsername()) : null)
                .email(entity.getEmail() != null ? Email.of(entity.getEmail()) : null)
                .hashPassword(entity.getHashPassword())
                .refreshToken(entity.getRefreshToken())
                .accountType(entity.getAccountType())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .gender(entity.getGender())
                .dob(entity.getDob() != null ? Dob.of(entity.getDob()) : null)
                .jlptLevel(entity.getJlptLevel())
                .status(entity.getStatus())
                .currentStreak(entity.getCurrentStreak())
                .longestStreak(entity.getLongestStreak())
                .lastPracticeDate(entity.getLastPracticeDate())
                .roles(entity.getRoles() != null ? entity.getRoles().stream()
                        .map(roleEntity -> Role.builder()
                                .id(roleEntity.getId())
                                .roleName(roleEntity.getRoleName())
                                .description(roleEntity.getDescription())
                                .build())
                        .toList() : List.of())
                .build();
    }
}
