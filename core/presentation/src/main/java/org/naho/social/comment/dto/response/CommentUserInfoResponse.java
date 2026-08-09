package org.naho.social.comment.dto.response;

import java.util.List;

public record CommentUserInfoResponse(
        Long id,
        Long league_id,
        Integer rank,
        String full_name,
        String avatar_object_key,
        List<String> oauth_avatar_url,
        Double total_point
) {
}
