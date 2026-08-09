package org.naho.social.reaction.result;

import org.naho.social.reaction.type.ReactionAction;
import org.naho.social.reaction.type.ReactionType;

public record ReactionResult(
        Long commentId,
        ReactionType reactionType,
        ReactionAction reactionAction,
        String fullName
) {
}
