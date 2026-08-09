package org.naho.social.comment.port.in;

import org.naho.social.comment.command.CommentCreateCommand;
import org.naho.social.comment.command.CommentDeleteCommand;
import org.naho.social.comment.command.CommentReadCommand;
import org.naho.social.comment.command.CommentUpdateCommand;
import org.naho.social.comment.result.CommentListResult;
import org.naho.social.comment.result.CommentResult;

public interface CommentCrudInputPort {
    CommentListResult getListCommentOfQuestion(CommentReadCommand command, Long currentUserId);

    CommentResult createComment(CommentCreateCommand command);

    CommentResult updateComment(CommentUpdateCommand command);

    void deleteComment(CommentDeleteCommand command);
}
