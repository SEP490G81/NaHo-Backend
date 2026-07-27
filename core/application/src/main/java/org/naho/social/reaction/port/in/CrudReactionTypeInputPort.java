package org.naho.social.reaction.port.in;

import org.naho.social.reaction.command.ReactionActionCommand;
import org.naho.social.reaction.result.ReactionDetailResult;
import org.naho.social.reaction.result.ReactionResult;

public interface CrudReactionTypeInputPort {
    ReactionResult chooseReaction(ReactionActionCommand reactionActionCommand);

    ReactionDetailResult getReactionsByComment(Long commentId);
}
