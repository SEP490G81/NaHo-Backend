package org.naho.user.result;

import java.util.List;

public record LeaderboardUserResult(
        Long id,
        Integer rank,
        String username,
        String email,
        String fullName,
        String avatarObjectKey,
        List<String> oAuthAvatarUrl,
        Double totalPoint
) {
}
