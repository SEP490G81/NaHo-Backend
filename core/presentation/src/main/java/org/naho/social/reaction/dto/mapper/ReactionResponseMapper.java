package org.naho.social.reaction.dto.mapper;

import org.naho.social.reaction.dto.response.ReactionDetailResponse;
import org.naho.social.reaction.dto.response.ReactionResponse;
import org.naho.social.reaction.result.ReactionDetailResult;
import org.naho.social.reaction.result.ReactionResult;
import org.naho.social.reaction.type.ReactionType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ReactionResponseMapper {

    public ReactionResponse resultToResponse(ReactionResult result) {
        if (result == null) return null;
        return new ReactionResponse(result.reactionType(), result.fullName());
    }

    public ReactionDetailResponse resultToDetailResponse(ReactionDetailResult result) {
        if (result == null) return null;

        Map<ReactionType, List<ReactionDetailResponse.ReactionUserItem>> users = result.users().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream()
                                .map(u -> new ReactionDetailResponse.ReactionUserItem(u.userId(), u.fullName()))
                                .toList()
                ));

        return new ReactionDetailResponse(
                result.commentId(),
                result.total(),
                result.counts(),
                users
        );
    }
}
