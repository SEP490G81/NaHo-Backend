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

    public CommentCreateCommand requestToCommand(CreateCommentRequest createCommentRequest) {
        return requestToCommand(createCommentRequest, createCommentRequest.userId());
    }

    public CommentUpdateCommand requestToUpdateCommand(UpdateCommentRequest updateCommentRequest, Long userId) {
        return new CommentUpdateCommand(
                updateCommentRequest.commentId(),
                userId != null ? userId : updateCommentRequest.userId(),
                updateCommentRequest.questionId(),
                updateCommentRequest.newContent(),
                updateCommentRequest.parentId()
        );
    }

    public CommentUpdateCommand requestToUpdateCommand(UpdateCommentRequest updateCommentRequest) {
        return requestToUpdateCommand(updateCommentRequest, updateCommentRequest.userId());
    }

    public CommentDeleteCommand requestToDeleteCommand(DeleteCommandRequest deleteCommandRequest, Long userId) {
        return new CommentDeleteCommand(
                deleteCommandRequest.commentId(),
                userId
        );
    }

    public CommentDeleteCommand requestToDeleteCommand(DeleteCommandRequest deleteCommandRequest) {
        return requestToDeleteCommand(deleteCommandRequest, null);
    }
}
