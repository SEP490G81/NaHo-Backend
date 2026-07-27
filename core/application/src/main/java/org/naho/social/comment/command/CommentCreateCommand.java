package org.naho.social.comment.command;

public record CommentCreateCommand(
        Long userId,
        Long speakingQuestionId,
        String content,
        Long parentId
) {
}
