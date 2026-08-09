package org.naho.user.dto.response;

import java.util.List;

public record LeaderboardUserResponse(
        Long id,
        Long leagueId,
        Integer rank,
        String fullName,
        String avatarUrl,
        List<String> authAvatarUrl,
        Double totalPoint
) {
}
