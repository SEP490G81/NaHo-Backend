package org.naho.social.comment.dto.mapper;

import org.naho.social.comment.dto.response.CommentResponse;
import org.naho.social.comment.result.CommentResonseResult;

public class CommentResponseMapper {
    public CommentResponse resultToResponse(CommentResonseResult result) {
        return new CommentResponse(
                result.commentId(),
                result.questionId(),
                result.userId(),
                result.username(),
                result.avatarUrl(),
                result.parentId(),
                result.content(),
                result.createdTime(),
                result.reactionSummary()
        );
    }
}
