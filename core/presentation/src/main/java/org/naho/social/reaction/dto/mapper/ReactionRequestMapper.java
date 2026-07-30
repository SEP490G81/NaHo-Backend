package org.naho.social.reaction.dto.mapper;

import org.naho.social.reaction.command.ReactionActionCommand;
import org.naho.social.reaction.dto.request.ReactionRequest;
import org.naho.social.reaction.type.ReactionAction;

public class ReactionRequestMapper {

    public ReactionActionCommand toCommand(ReactionRequest request, Long userId) {
        return new ReactionActionCommand(
                request.commentId(),
                userId,
                request.reactionType(),
                request.reactionAction() != null ? request.reactionAction() : ReactionAction.ADDED
        );
    }
}
