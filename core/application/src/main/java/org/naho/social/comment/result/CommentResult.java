package org.naho.social.comment.result;

import org.naho.social.reaction.type.ReactionType;
import org.naho.user.result.LeaderboardUserResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record CommentResult(
        Long commentId,
        Long questionId,
        LeaderboardUserResult userInfo,
        Long parentId,
        String content,
        LocalDateTime createdTime,
        LocalDateTime modifiedTime,
        ReactionSummaryResult reactionSummary,
        List<CommentResult> children
) {
    public record ReactionSummaryResult(
            long total,
            Map<ReactionType, Long> counts,
            ReactionType myReaction
    ) {
    }
}
