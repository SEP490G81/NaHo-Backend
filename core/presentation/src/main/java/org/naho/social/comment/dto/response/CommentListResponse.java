package org.naho.social.comment.dto.response;

import java.util.List;

public record CommentListResponse(
        Long speakingQuestionId,
        List<CommentResponse> comments
) {
}
