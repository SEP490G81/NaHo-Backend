package org.naho.social.comment.dto.response;

import org.naho.social.reaction.type.ReactionType;
import org.naho.user.dto.response.LeaderboardUserResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record CommentResponse(
        Long commentId,
        Long questionId,
        LeaderboardUserResponse userInfo,
        Long parentId,
        String content,
        LocalDateTime createdTime,
        LocalDateTime modifiedTime,
        ReactionSummaryResponse reactionSummary,
        List<CommentResponse> children
) {
    public record ReactionSummaryResponse(
            long total,
            Map<ReactionType, Long> counts,
            ReactionType myReaction
    ) {
    }
}
