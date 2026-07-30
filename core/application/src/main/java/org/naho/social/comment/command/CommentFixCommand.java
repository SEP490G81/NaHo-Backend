package org.naho.social.comment.command;

public record CommentFixCommand(
        Long commentId,
        Long userId,
        String newContent
) {
}
