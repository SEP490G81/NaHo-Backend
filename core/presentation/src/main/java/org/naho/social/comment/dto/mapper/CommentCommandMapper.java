package org.naho.social.comment.dto.mapper;

import org.naho.social.comment.command.CommentCreateCommand;
import org.naho.social.comment.command.CommentDeleteCommand;
import org.naho.social.comment.command.CommentUpdateCommand;
import org.naho.social.comment.dto.request.CreateCommentRequest;
import org.naho.social.comment.dto.request.DeleteCommandRequest;
import org.naho.social.comment.dto.request.UpdateCommentRequest;

public class CommentCommandMapper {

    public CommentCreateCommand requestToCommand(CreateCommentRequest createCommentRequest, Long userId) {
        return new CommentCreateCommand(
                userId != null ? userId : createCommentRequest.userId(),
                createCommentRequest.speakingQuestionId(),
                createCommentRequest.content(),
                createCommentRequest.parentId()
        );
    }

    public CommentUpdateCommand requestToUpdateCommand(UpdateCommentRequest updateCommentRequest, Long userId) {
        return new CommentUpdateCommand(
                updateCommentRequest.commentId(),
                userId,
                updateCommentRequest.newContent()
        );
    }

    public CommentDeleteCommand requestToDeleteCommand(DeleteCommandRequest deleteCommandRequest, Long userId, boolean isAdmin) {
        return new CommentDeleteCommand(
                deleteCommandRequest.commentId(),
                userId,
                isAdmin
        );
    }
}
