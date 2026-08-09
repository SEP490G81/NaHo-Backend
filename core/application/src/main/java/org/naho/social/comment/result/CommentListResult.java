package org.naho.social.comment.result;

import java.util.List;

public record CommentListResult(
        Long speakingQuestionId,
        List<CommentResult> comments
) {
}
