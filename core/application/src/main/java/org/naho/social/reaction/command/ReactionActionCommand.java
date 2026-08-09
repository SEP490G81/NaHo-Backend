package org.naho.social.reaction.command;

import org.naho.social.reaction.type.ReactionType;

public record ReactionActionCommand(
        Long comment_id,
        Long userId,
        ReactionType reactionType
) {

}
