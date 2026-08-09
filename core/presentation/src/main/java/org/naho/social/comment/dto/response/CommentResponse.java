package org.naho.social.comment.dto.response;

import org.naho.social.comment.result.CommentResponseResult;

import java.time.LocalDateTime;
import java.util.List;

public record CommentResponse(
        Long comment_id,
        Long question_id,
        Long user_id,
        String full_name,
        String avatar_url,
        Integer rank,
        CommentUserInfoResponse user_info,
        Long parent_id,
        String content,
        LocalDateTime created_time,
        CommentResponseResult.ReactionSummary reaction_summary,
        List<CommentResponse> children
) {
}
