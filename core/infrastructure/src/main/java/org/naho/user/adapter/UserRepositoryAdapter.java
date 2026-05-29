package org.naho.user.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.user.mapper.UserEntityMapper;
import org.naho.user.model.User;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.repository.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
    public boolean existsByUsername(String username) {
        return userJpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public User save(User user) {
        return null; // tạm thời để merging
    }

//    @Override
//    public User save(User user) {
//        // map từ domain sang infra...
//        UserEntity entity = UserEntity.builder()
//                .username(user.getUsername().getValue())
//                .email(user.getEmail().getValue())
//                .hashPassword(user.getHashPassword())
//                .accountType(user.getAccountType())
//                .status(user.getStatus())
//                .currentStreak(user.getCurrentStreak())
//                .longestStreak(user.getLongestStreak())
//                .jlptLevel(user.getJlptLevel()!=null? user.getJlptLevel() : JLPTLevel.N5)
//                .build();
//
//        entity.setCreatedBy(0L);
//        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
//            Set<RoleEntity> roleEntities = user.getRoles().stream()
//                    .map(role -> {
//                        RoleEntity roleEntity = new RoleEntity();
//                        roleEntity.setId(role.getId());
//                        roleEntity.setRoleName(role.getRoleName());
//                        roleEntity.setDescription(role.getDescription());
//                        return roleEntity;
//                    }).collect(Collectors.toSet());
//            entity.setRoles(roleEntities);
//        } else {
//            entity.setRoles(Collections.emptySet());
//        }
//
//        UserEntity savedEntity = userJpaRepository.save(entity);
//
//        // map từ entity về domain
//        return User.builder()
//                .id(savedEntity.getId())
//                .username(org.naho.user.valueobject.Username.of(savedEntity.getUsername()))
//                .email(org.naho.user.valueobject.Email.of(savedEntity.getEmail()))
//                .hashPassword(savedEntity.getHashPassword())
//                .accountType(savedEntity.getAccountType())
//                .status(savedEntity.getStatus())
//                .currentStreak(savedEntity.getCurrentStreak())
//                .longestStreak(savedEntity.getLongestStreak())
//                .jlptLevel(savedEntity.getJlptLevel())
//                .build();
//    }
}
