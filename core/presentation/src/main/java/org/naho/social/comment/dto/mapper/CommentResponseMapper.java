package org.naho.social.comment.dto.mapper;

import org.naho.social.comment.dto.response.CommentListResponse;
import org.naho.social.comment.dto.response.CommentResponse;
import org.naho.social.comment.dto.response.CommentUserInfoResponse;
import org.naho.social.comment.result.CommentListResponseResult;
import org.naho.social.comment.result.CommentResponseResult;
import org.naho.user.result.LeaderboardUserResult;

import java.util.List;

public class CommentResponseMapper {

    public CommentResponse resultToResponse(CommentResponseResult result) {
        if (result == null) return null;

        CommentUserInfoResponse userInfoResponse = null;
        if (result.userInfo() != null) {
            LeaderboardUserResult info = result.userInfo();
            userInfoResponse = new CommentUserInfoResponse(
                    info.getId(),
                    info.getLeagueId(),
                    info.getRank(),
                    info.getFullName(),
                    info.getAvatarObjectKey(),
                    info.getoAuthAvatarUrl(),
                    info.getTotalPoint()
            );
        }

        List<CommentResponse> childResponses = null;
        if (result.children() != null) {
            childResponses = result.children().stream()
                    .map(this::resultToResponse)
                    .toList();
        }

        return new CommentResponse(
                result.commentId(),
                result.questionId(),
                result.userId(),
                result.fullName(),
                result.avatarUrl(),
                result.rank(),
                userInfoResponse,
                result.parentId(),
                result.content(),
                result.createdTime(),
                result.reactionSummary(),
                childResponses
        );
    }

    public CommentListResponse listResultToListResponse(CommentListResponseResult listResult) {
        if (listResult == null) return null;
        List<CommentResponse> commentResponses = listResult.comments() != null
                ? listResult.comments().stream().map(this::resultToResponse).toList()
                : List.of();
        return new CommentListResponse(listResult.speakingQuestionId(), commentResponses);
    }
}
