package org.naho.user.adapter;

import org.naho.user.entity.RoleEntity;
import org.naho.user.entity.UserEntity;
import org.naho.user.model.User;
import org.naho.user.port.repository.UserRepository;
import org.naho.user.repository.UserJpaRepository;
import org.naho.user.type.JLPTLevel;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryAdapter implements UserRepository {
    private final UserJpaRepository userJpaRepository;

    public UserRepositoryAdapter(UserJpaRepository userJpaRepository) {this.userJpaRepository = userJpaRepository;}

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.empty();
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
    public User save(User user) {
        // map từ domain sang infra...
        UserEntity entity = UserEntity.builder()
                .username(user.getUsername().getValue())
                .email(user.getEmail().getValue())
                .hashPassword(user.getHashPassword())
                .accountType(user.getAccountType())
                .status(user.getStatus())
                .currentStreak(user.getCurrentStreak())
                .longestStreak(user.getLongestStreak())
                .jlptLevel(user.getJlptLevel()!=null? user.getJlptLevel() : JLPTLevel.N5)
                .build();

        entity.setCreatedBy(0L);
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            Set<RoleEntity> roleEntities = user.getRoles().stream()
                    .map(role -> {
                        RoleEntity roleEntity = new RoleEntity();
                        roleEntity.setId(role.getId());
                        roleEntity.setRoleName(role.getRoleName());
                        roleEntity.setDescription(role.getDescription());
                        return roleEntity;
                    }).collect(Collectors.toSet());
            entity.setRoles(roleEntities);
        } else {
            entity.setRoles(Collections.emptySet());
        }

        UserEntity savedEntity = userJpaRepository.save(entity);

        // map từ entity về domain
        return User.builder()
                .id(savedEntity.getId())
                .username(org.naho.user.valueobject.Username.of(savedEntity.getUsername()))
                .email(org.naho.user.valueobject.Email.of(savedEntity.getEmail()))
                .hashPassword(savedEntity.getHashPassword())
                .accountType(savedEntity.getAccountType())
                .status(savedEntity.getStatus())
                .currentStreak(savedEntity.getCurrentStreak())
                .longestStreak(savedEntity.getLongestStreak())
                .jlptLevel(savedEntity.getJlptLevel())
                .build();
    }
}
