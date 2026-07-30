package org.naho.social.comment.command;

public record CommentUpdateCommand(
        Long commentId,
        Long userId,
        Long questionId,
        String newContent,
        Long parentId
) {
}
