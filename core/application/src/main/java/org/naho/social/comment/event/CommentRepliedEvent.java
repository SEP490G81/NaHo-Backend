package org.naho.social.comment.event;

public record CommentRepliedEvent(
        Long parentAuthorId,
        Long replierId,
        Long questionId,
        Long newCommentId
) {
}
