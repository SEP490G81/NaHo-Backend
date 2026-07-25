package org.naho.learning.mapper;

import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.learning.model.UserLearningProgress;
import org.naho.learning.result.UserLearningProgressResult;
import org.naho.shared.exception.ApplicationException;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.result.LeaderboardUserResult;

public class UserLearningProgressResultMapper {

    private final UserRepositoryPort userRepositoryPort;

    public UserLearningProgressResultMapper(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    public UserLearningProgressResult domainToResult(UserLearningProgress domain) {
        return UserLearningProgressResult.builder()
                .id(domain.getId())
                .farthestAvailableNodeId(domain.getFarthestAvailableNodeId())
                .farthestAvailableNodeGlobalOrderIndex(domain.getFarthestAvailableNodeGlobalOrderIndex())
                .lastLearningNodeId(domain.getLastLearningNodeId())
                .lastLearningNodeGlobalOrderIndex(domain.getLastLearningNodeGlobalOrderIndex())
                .lastLearningAt(domain.getLastLearningAt())
                .currentStreak(domain.getCurrentStreak())
                .longestStreak(domain.getLongestStreak())
                .totalPoint(domain.getTotalPoint())
                .build();
    }

    public UserLearningProgressResult domainToDetailsResult(UserLearningProgress domain, Long userId) {
        LeaderboardUserResult leaderboardUserResult = userRepositoryPort
                .findTopOfUserByUserId(userId)
                .orElseThrow(() -> new ApplicationException(
                        UserErrorCode.USER_NOT_FOUND,
                        UserDetailMessageKey.USER_ID_NOT_FOUND
                ));

        UserLearningProgressResult userLearningProgress = domainToResult(domain);
        userLearningProgress.setLeaderboardUser(leaderboardUserResult);

        return userLearningProgress;
    }
}
