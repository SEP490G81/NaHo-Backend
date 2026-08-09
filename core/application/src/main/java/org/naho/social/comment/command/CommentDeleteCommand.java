package org.naho.social.comment.command;

public record CommentDeleteCommand(
        Long commentId,
        Long userId,
        boolean isAdmin
) {
    public CommentDeleteCommand(Long commentId, Long userId) {
        this(commentId, userId, false);
    }
}
