package org.naho.social.reaction.result;

import org.naho.social.reaction.type.ReactionAction;
import org.naho.social.reaction.type.ReactionType;

public record ReactionEventResult(
        Long commentId,
        ReactionType reactionType,
        Long count,
        ReactionAction action,
        Long userId
) {
}