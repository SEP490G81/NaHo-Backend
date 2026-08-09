package org.naho.social.reaction.dto.request;

import jakarta.validation.constraints.NotNull;
import org.naho.social.reaction.type.ReactionType;

public record ReactionRequest(
        @NotNull(message = "reaction.comment.id.blank")
        Long commentId,
        @NotNull(message = "reaction.type.blank")
        ReactionType reactionType
) {
}
