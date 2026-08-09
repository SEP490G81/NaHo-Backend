package org.naho.social.comment.dto.response;

import org.naho.social.comment.result.CommentResonseResult;

import java.time.LocalDateTime;

public record CommentResponse(
        Long comment_id,
        Long question_id,
        Long user_id,
        String username,
        String avatar_url,
        Long parent_id,
        String content,
        LocalDateTime created_time,
        CommentResonseResult.ReactionSummary reaction_summary
) {
}
