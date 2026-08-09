package org.naho.social.comment.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;

public record UpdateCommentRequest(
        @JsonAlias({"comment_id", "commentId"})
        Long commentId,

        @JsonAlias({"newContent", "content"})
        String newContent
) {
}
