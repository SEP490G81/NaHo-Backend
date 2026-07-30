package org.naho.social.comment.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;

public record CreateCommentRequest(
        @JsonAlias({"speaking_question_id", "question_id", "questionId"})
        Long speakingQuestionId,

        @JsonAlias({"user_id", "userId"})
        Long userId,

        @JsonAlias({"parent_id", "parentId"})
        Long parentId,

        String content
) {
}
