package org.naho.social.comment.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;

public record UpdateCommentRequest(
        @JsonAlias({"comment_id", "commentId"})
        Long commentId,

        @JsonAlias({"speaking_question_id", "question_id", "questionId", "speakingQuestionId"})
        Long questionId,

        @JsonAlias({"user_id", "userId"})
        Long userId,

        @JsonAlias({"newContent", "content"})
        String newContent,

        @JsonAlias({"parent_id", "parentId"})
        Long parentId
) {
}
