package org.naho.social.comment.dto.mapper;

import org.naho.social.comment.dto.response.CommentResponse;
import org.naho.social.comment.result.CommentResonseResult;

public class CommentResponseMapper {
    public CommentResponse resultToResponse(CommentResonseResult result) {
        return new CommentResponse(
                result.commentId(),
                result.userId(),
                result.content()
        );
    }
}
