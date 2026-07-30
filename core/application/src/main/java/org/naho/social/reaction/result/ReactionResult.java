package org.naho.social.reaction.result;

import org.naho.social.reaction.type.ReactionType;

public record ReactionResult(
        ReactionType reactionType,
        String username
) {
}
