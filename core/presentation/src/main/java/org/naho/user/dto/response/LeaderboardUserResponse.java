package org.naho.user.dto.response;

import java.util.List;

public record LeaderboardUserResponse(
        Long id,
        Long leagueId,
        Integer rank,
        String username,
        String email,
        String fullName,
        String avatarObjectKey,
        List<String> oAuthAvatarUrl,
        Double totalPoint
) {
}
