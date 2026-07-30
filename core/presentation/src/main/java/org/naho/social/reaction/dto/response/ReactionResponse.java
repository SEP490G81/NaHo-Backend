package org.naho.social.reaction.dto.response;

import org.naho.social.reaction.type.ReactionType;

public record ReactionResponse(
        ReactionType reactionType,
        String username
) {
}
