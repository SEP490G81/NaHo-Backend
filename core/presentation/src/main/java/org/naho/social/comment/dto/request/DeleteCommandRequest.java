package org.naho.social.comment.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;

public record DeleteCommandRequest(
        @JsonAlias({"comment_id", "commentId"})
        Long commentId
) {
}
