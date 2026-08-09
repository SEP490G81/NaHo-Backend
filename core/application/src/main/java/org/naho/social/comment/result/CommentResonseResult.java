package org.naho.social.comment.result;

import org.naho.social.reaction.type.ReactionType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record CommentResonseResult(
        Long commentId,
        Long questionId,
        Long userId,
        String username,
        String avatarUrl,
        Long parentId,
        String content,
        LocalDateTime createdTime,
        LocalDateTime modifiedTime,
        ReactionSummary reactionSummary,
        List<CommentResonseResult> children
) {
    public record ReactionSummary(
            long total,
            Map<ReactionType, Long> counts,
            ReactionType myReaction
    ) {
    }
}
