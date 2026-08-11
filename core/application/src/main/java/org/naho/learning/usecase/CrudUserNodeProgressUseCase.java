package org.naho.learning.usecase;

import org.naho.i18n.message.learning.UserNodeProgressDetailMessageKey;
import org.naho.learning.exception.UserNodeProgressErrorCode;
import org.naho.learning.mapper.UserNodeProgressResultMapper;
import org.naho.learning.model.UserNodeProgress;
import org.naho.learning.port.in.CrudUserNodeProgressPort;
import org.naho.learning.port.out.UserNodeProgressRepositoryPort;
import org.naho.learning.result.UserNodeProgressResult;
import org.naho.shared.exception.ApplicationException;

public class CrudUserNodeProgressUseCase implements CrudUserNodeProgressPort {
    private final UserNodeProgressRepositoryPort userNodeProgressRepositoryPort;
    private final UserNodeProgressResultMapper userNodeProgressResultMapper;

    public CrudUserNodeProgressUseCase(
            UserNodeProgressRepositoryPort userNodeProgressRepositoryPort,
            UserNodeProgressResultMapper userNodeProgressResultMapper
    ) {
        this.userNodeProgressRepositoryPort = userNodeProgressRepositoryPort;
        this.userNodeProgressResultMapper = userNodeProgressResultMapper;
    }

    @Override
    public UserNodeProgressResult findByLearningPathNodeIdAndUserId(Long learningPathNodeId, Long userId) {
        UserNodeProgress userNodeProgress = userNodeProgressRepositoryPort
                .findByLearningPathNodeIdAndUserId(learningPathNodeId, userId)
                .orElseThrow(() -> new ApplicationException(
                        UserNodeProgressErrorCode.USER_NODE_PROGRESS_NOT_FOUND,
                        UserNodeProgressDetailMessageKey.USER_NODE_PROGRESS_NOT_FOUND,
                        learningPathNodeId,
                        userId
                ));

        return userNodeProgressResultMapper.domainToResult(userNodeProgress);
    }
}

