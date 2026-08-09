package org.naho.social.comment.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.social.comment.dto.response.CommentListResponse;
import org.naho.social.comment.dto.response.CommentResponse;
import org.naho.social.comment.result.CommentListResult;
import org.naho.social.comment.result.CommentResult;
import org.naho.user.dto.mapper.UserResponseMapper;

@Mapper(componentModel = "spring", uses = {UserResponseMapper.class})
public interface CommentResponseMapper {
    CommentResponse resultToResponse(CommentResult result);

    CommentResult.ReactionSummaryResult resultToReactionSummary(CommentResult.ReactionSummaryResult result);

    CommentListResponse resultToResponse(CommentListResult result);
}
