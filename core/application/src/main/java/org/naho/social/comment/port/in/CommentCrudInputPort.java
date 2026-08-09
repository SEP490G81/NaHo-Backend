package org.naho.social.comment.port.in;

import org.naho.social.comment.command.CommentCreateCommand;
import org.naho.social.comment.command.CommentDeleteCommand;
import org.naho.social.comment.command.CommentReadCommand;
import org.naho.social.comment.command.CommentUpdateCommand;
import org.naho.social.comment.result.CommentListResponseResult;
import org.naho.social.comment.result.CommentResponseResult;

public interface CommentCrudInputPort {
    CommentListResponseResult getListCommentOfQuestion(CommentReadCommand command, Long currentUserId);

    CommentResponseResult createComment(CommentCreateCommand command);

    CommentResponseResult updateComment(CommentUpdateCommand command);

    CommentResponseResult deleteComment(CommentDeleteCommand command);
}
