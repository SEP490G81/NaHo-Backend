package org.naho.social.comment.command;

public record CommentDeleteCommand(
        Long commentId,
        Long userId
) {
}
