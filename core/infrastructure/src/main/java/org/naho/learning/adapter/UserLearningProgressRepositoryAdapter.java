package org.naho.learning.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.learning.entity.UserLearningProgressEntity;
import org.naho.learning.mapper.UserLearningProgressEntityMapper;
import org.naho.learning.model.UserLearningProgress;
import org.naho.learning.port.out.UserLearningProgressRepositoryPort;
import org.naho.learning.repository.LearningPathNodeJpaRepository;
import org.naho.learning.repository.UserLearningProgressJpaRepository;
import org.naho.shared.exception.ApplicationException;
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
    private final LearningPathNodeJpaRepository learningPathNodeJpaRepository;
    private final UserLearningProgressEntityMapper userLearningProgressEntityMapper;

    @Override
    public Optional<UserLearningProgress> findUserLearningProgressByUserId(Long userId) {
        return userLearningProgressJpaRepository
                .findByUserId(userId)
                .map(userLearningProgressEntityMapper::entityToDomain);
    }

    @Override
    public UserLearningProgress save(UserLearningProgress userLearningProgress, Long userId) {
        UserLearningProgressEntity entity = userLearningProgressEntityMapper.domainToEntity(userLearningProgress);

        UserEntity user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(
                        UserErrorCode.USER_NOT_FOUND,
                        UserDetailMessageKey.USER_ID_NOT_FOUND,
                        userId
                ));

        entity.setUser(user);

        if (userLearningProgress.getFarthestAvailableNodeId() != null) {
            entity.setFarthestAvailableNode(learningPathNodeJpaRepository.getReferenceById(userLearningProgress.getFarthestAvailableNodeId()));
        }
        if (userLearningProgress.getLastLearningNodeId() != null) {
            entity.setLastLearningNode(learningPathNodeJpaRepository.getReferenceById(userLearningProgress.getLastLearningNodeId()));
        }

        UserLearningProgressEntity savedEntity = userLearningProgressJpaRepository.save(entity);

        user.setUserLearningProgress(savedEntity);
        userJpaRepository.save(user);

        return userLearningProgressEntityMapper.entityToDomain(savedEntity);
    }
}
