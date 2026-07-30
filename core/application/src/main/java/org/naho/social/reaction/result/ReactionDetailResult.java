package org.naho.social.reaction.result;

import org.naho.social.reaction.type.ReactionType;

import java.util.List;
import java.util.Map;

public record ReactionDetailResult(
        Long commentId,
        long total,
        Map<ReactionType, Long> counts,
        Map<ReactionType, List<ReactionUserItem>> users
) {
    public record ReactionUserItem(
            Long userId,
            String username
    ) {
    }
}
