package org.naho.learning.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.learning.entity.LearningPathNodeEntity;
import org.naho.learning.entity.UserNodeProgressEntity;
import org.naho.learning.mapper.UserNodeProgressEntityMapper;
import org.naho.learning.model.UserNodeProgress;
import org.naho.learning.port.out.UserNodeProgressRepositoryPort;
import org.naho.learning.repository.LearningPathNodeJpaRepository;
import org.naho.learning.repository.UserNodeProgressJpaRepository;
import org.naho.user.entity.UserEntity;
import org.naho.user.repository.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserNodeProgressRepositoryAdapter implements UserNodeProgressRepositoryPort {
    private final UserNodeProgressJpaRepository userNodeProgressJpaRepository;
    private final UserNodeProgressEntityMapper userNodeProgressEntityMapper;
    private final LearningPathNodeJpaRepository learningPathNodeJpaRepository;
    private final UserJpaRepository userJpaRepository;

    @Override
    public boolean existsByLearningPathNodeId(Long learningPathNodeId) {
        return userNodeProgressJpaRepository.existsByLearningPathNode_Id(learningPathNodeId);
    }

    @Override
    public UserNodeProgress save(UserNodeProgress userNodeProgress) {
        UserNodeProgressEntity userNodeProgressEntity =
                userNodeProgressEntityMapper.domainToEntity(userNodeProgress);

        LearningPathNodeEntity learningPathNodeEntity =
                learningPathNodeJpaRepository.getReferenceById(userNodeProgress.getLearningPathNodeId());

        UserEntity userEntity =
                userJpaRepository.getReferenceById(userNodeProgress.getUserId());

        userNodeProgressEntity.setUser(userEntity);
        userNodeProgressEntity.setLearningPathNode(learningPathNodeEntity);
        UserNodeProgressEntity savedEntity =
                userNodeProgressJpaRepository.save(userNodeProgressEntity);

        return userNodeProgressEntityMapper.entityToDomain(savedEntity);
    }

    @Override
    public Optional<UserNodeProgress> findByLearningPathNodeIdAndUserId(Long learningPathNodeId, Long userId) {
        return userNodeProgressJpaRepository
                .findByLearningPathNode_IdAndUser_Id(learningPathNodeId, userId)
                .map(userNodeProgressEntityMapper::entityToDomain);
    }
}
