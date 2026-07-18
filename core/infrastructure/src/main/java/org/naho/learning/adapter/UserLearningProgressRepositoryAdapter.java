package org.naho.learning.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.learning.entity.UserLearningProgressEntity;
import org.naho.learning.mapper.UserLearningProgressEntityMapper;
import org.naho.learning.model.UserLearningProgress;
import org.naho.learning.port.out.UserLearningProgressRepositoryPort;
import org.naho.learning.repository.UserLearningProgressJpaRepository;
import org.naho.shared.exception.InfrastructureException;
import org.naho.user.entity.UserEntity;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.repository.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserLearningProgressRepositoryAdapter implements UserLearningProgressRepositoryPort {

    private final UserLearningProgressJpaRepository userLearningProgressJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final UserLearningProgressEntityMapper userLearningProgressEntityMapper;

    @Override
    public UserLearningProgress createNew(UserLearningProgress userLearningProgress, Long userId) {
        UserLearningProgressEntity userLearningProgressEntity =
                userLearningProgressEntityMapper.domainToEntity(userLearningProgress);

        UserEntity user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new InfrastructureException(
                        UserErrorCode.USER_NOT_FOUND,
                        UserDetailMessageKey.USER_ID_NOT_FOUND,
                        userId
                ));

        userLearningProgressEntity.setUser(user);

        UserLearningProgressEntity savedUserLearningProgressEntity =
                userLearningProgressJpaRepository.save(userLearningProgressEntity);

        user.setUserLearningProgress(savedUserLearningProgressEntity);
        userJpaRepository.save(user);

        return userLearningProgressEntityMapper.entityToDomain(savedUserLearningProgressEntity);
    }

    @Override
    public Optional<UserLearningProgress> findUserLearningProgressByUserId(Long userId) {
        return userLearningProgressJpaRepository
                .findByUserId(userId)
                .map(userLearningProgressEntityMapper::entityToDomain);
    }

    @Override
    public UserLearningProgress save(UserLearningProgress userLearningProgress) {
        UserLearningProgressEntity userLearningProgressEntity =
                userLearningProgressEntityMapper.domainToEntity(userLearningProgress);

        UserLearningProgressEntity savedEntity =
                userLearningProgressJpaRepository.save(userLearningProgressEntity);
        return userLearningProgressEntityMapper.entityToDomain(savedEntity);
    }
}
