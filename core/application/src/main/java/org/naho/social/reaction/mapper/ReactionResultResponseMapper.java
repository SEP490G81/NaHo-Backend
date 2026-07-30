package org.naho.social.reaction.mapper;

import org.naho.social.reaction.model.Reaction;
import org.naho.social.reaction.result.ReactionResult;

public class ReactionResultResponseMapper {

    public ReactionResult domainToResult(
            Reaction reaction,
            String username
    ) {
        return new ReactionResult(
                reaction.getReactionType(),
                username
        );
    }
}
