package org.naho.social.reaction.mapper;

import org.naho.social.reaction.command.ReactionActionCommand;
import org.naho.social.reaction.model.Reaction;

public class ReactionActionCommandMapper {

    public Reaction commandToDomain(ReactionActionCommand reactionActionCommand) {
        return Reaction.builder().userId(
                reactionActionCommand.userId()
        ).commentId(
                reactionActionCommand.comment_id()
        ).reactionType(reactionActionCommand.reactionType()).build();
    }
}
