package org.naho.social.comment.dto.response;

public record CommentResponse(
        Long comment_id,
        Long user_id,
        String content
) {
}
