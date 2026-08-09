package org.naho.social.reaction.mapper;

import org.naho.social.reaction.model.Reaction;
import org.naho.social.reaction.result.ReactionResult;
import org.naho.social.reaction.type.ReactionAction;

public class ReactionResultResponseMapper {

    public ReactionResult domainToResult(
            Reaction reaction,
            ReactionAction action,
            String fullName
    ) {
        return new ReactionResult(
                reaction.getCommentId(),
                reaction.getReactionType(),
                action,
                fullName
        );
    }
}
