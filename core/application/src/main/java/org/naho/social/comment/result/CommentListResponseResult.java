package org.naho.social.comment.result;

import java.util.List;

public record CommentListResponseResult(
        Long speakingQuestionId,
        List<CommentResponseResult> comments
) {
}
