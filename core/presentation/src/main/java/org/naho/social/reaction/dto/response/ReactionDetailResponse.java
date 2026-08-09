package org.naho.social.reaction.dto.response;

import org.naho.social.reaction.type.ReactionType;

import java.util.List;
import java.util.Map;

public record ReactionDetailResponse(
        Long commentId,
        long total,
        Map<ReactionType, Long> counts,
        Map<ReactionType, List<ReactionUserItem>> users
) {
    public record ReactionUserItem(
            Long userId,
            String fullName
    ) {
    }
}
