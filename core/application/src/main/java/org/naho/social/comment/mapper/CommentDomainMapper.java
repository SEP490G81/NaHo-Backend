package org.naho.social.comment.mapper;

import org.naho.social.comment.command.CommentCreateCommand;
import org.naho.social.comment.model.Comment;

public class CommentDomainMapper {
    public Comment commandToModel(CommentCreateCommand command) {
        return Comment.builder()
                .userId(command.userId())
                .questionId(command.speakingQuestionId())
                .content(command.content())
                .parentId(command.parentId())
                .build();
    }
}
