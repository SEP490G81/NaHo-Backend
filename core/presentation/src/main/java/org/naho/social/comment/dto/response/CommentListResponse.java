package org.naho.social.comment.dto.response;

import java.util.List;

public record CommentListResponse(
        Long speaking_question_id,
        List<CommentResponse> comments
) {
}
