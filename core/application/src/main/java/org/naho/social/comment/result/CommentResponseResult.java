package org.naho.social.comment.result;

import org.naho.social.reaction.type.ReactionType;
import org.naho.user.result.LeaderboardUserResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record CommentResponseResult(
        Long commentId,
        Long questionId,
        Long userId,
        String fullName,
        String avatarUrl,
        Integer rank,
        LeaderboardUserResult userInfo,
        Long parentId,
        String content,
        LocalDateTime createdTime,
        LocalDateTime modifiedTime,
        ReactionSummary reactionSummary,
        List<CommentResponseResult> children
) {
    public record ReactionSummary(
            long total,
            Map<ReactionType, Long> counts,
            ReactionType myReaction
    ) {
    }
}
