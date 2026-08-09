package org.naho.social.reaction.dto.response;

import org.naho.social.reaction.type.ReactionAction;
import org.naho.social.reaction.type.ReactionType;

public record ReactionResponse(
        Long commentId,
        ReactionType reactionType,
        ReactionAction reactionAction,
        String fullName
) {
}
