package org.naho.social.comment.mapper;

import org.naho.file.port.in.CrudFileInputPort;
import org.naho.file.result.FileResult;
import org.naho.social.comment.model.Comment;
import org.naho.social.comment.result.CommentResponseResult;
import org.naho.social.reaction.model.Reaction;
import org.naho.social.reaction.type.ReactionType;
import org.naho.user.model.User;
import org.naho.user.port.in.CrudAuthProviderInputPort;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.result.AuthProviderResult;
import org.naho.user.result.LeaderboardUserResult;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommentResultMapper {

    private final UserRepositoryPort userRepositoryPort;
    private final CrudFileInputPort crudFileInputPort;
    private final CrudAuthProviderInputPort crudAuthProviderInputPort;

    public CommentResultMapper() {
        this(null, null, null);
    }

    public CommentResultMapper(UserRepositoryPort userRepositoryPort,
                               CrudFileInputPort crudFileInputPort,
                               CrudAuthProviderInputPort crudAuthProviderInputPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.crudFileInputPort = crudFileInputPort;
        this.crudAuthProviderInputPort = crudAuthProviderInputPort;
    }

    public CommentResponseResult domainToResult(Comment comment) {
        UserInfo userInfo = fetchUserInfo(comment.getUserId());
        return new CommentResponseResult(
                comment.getId(),
                comment.getQuestionId(),
                comment.getUserId(),
                userInfo.fullName(),
                userInfo.avatarUrl(),
                userInfo.rank(),
                userInfo.leaderboardUserResult(),
                comment.getParentId(),
                comment.getContent(),
                toLocalDateTime(comment),
                toLocalDateTimeModified(comment),
                null,
                null
        );
    }

    public CommentResponseResult domainToResultWithReactions(
            Comment comment,
            List<Reaction> reactions,
            Long currentUserId
    ) {
        Map<ReactionType, Long> counts = reactions.stream()
                .collect(Collectors.groupingBy(Reaction::getReactionType, Collectors.counting()));

        ReactionType myReaction = reactions.stream()
                .filter(r -> r.getUserId().equals(currentUserId))
                .map(Reaction::getReactionType)
                .findFirst()
                .orElse(null);

        CommentResponseResult.ReactionSummary summary = new CommentResponseResult.ReactionSummary(
                reactions.size(),
                counts,
                myReaction
        );

        UserInfo userInfo = fetchUserInfo(comment.getUserId());

        return new CommentResponseResult(
                comment.getId(),
                comment.getQuestionId(),
                comment.getUserId(),
                userInfo.fullName(),
                userInfo.avatarUrl(),
                userInfo.rank(),
                userInfo.leaderboardUserResult(),
                comment.getParentId(),
                comment.getContent(),
                toLocalDateTime(comment),
                toLocalDateTimeModified(comment),
                summary,
                null
        );
    }

    private UserInfo fetchUserInfo(Long userId) {
        if (userId == null || userRepositoryPort == null) {
            return new UserInfo(null, null, null, null);
        }

        LeaderboardUserResult leaderboardUserResult = null;
        try {
            Optional<LeaderboardUserResult> topOpt = userRepositoryPort.findTopOfUserByUserId(userId);
            if (topOpt.isPresent()) {
                leaderboardUserResult = topOpt.get();
                leaderboardUserResult.setUsername(null);
            }
        } catch (Exception ignored) {
        }

        if (leaderboardUserResult == null) {
            return new UserInfo(null, null, null, null);
        }

        String fullName = (leaderboardUserResult.getFullName() != null && !leaderboardUserResult.getFullName().isBlank())
                ? leaderboardUserResult.getFullName()
                : null;

        Integer rank = leaderboardUserResult.getRank();

        String avatarUrl = null;
        if (leaderboardUserResult.getAvatarObjectKey() != null && !leaderboardUserResult.getAvatarObjectKey().isBlank()) {
            avatarUrl = leaderboardUserResult.getAvatarObjectKey();
        } else if (leaderboardUserResult.getoAuthAvatarUrl() != null && !leaderboardUserResult.getoAuthAvatarUrl().isEmpty()) {
            avatarUrl = leaderboardUserResult.getoAuthAvatarUrl().get(0);
        }

        return new UserInfo(fullName, avatarUrl, rank, leaderboardUserResult);
    }

    private LocalDateTime toLocalDateTime(Comment comment) {
        if (comment.getCreatedTime() == null) return null;
        return LocalDateTime.ofInstant(comment.getCreatedTime(), ZoneId.systemDefault());
    }

    private LocalDateTime toLocalDateTimeModified(Comment comment) {
        if (comment.getModifiedTime() == null) return null;
        return LocalDateTime.ofInstant(comment.getModifiedTime(), ZoneId.systemDefault());
    }

    private record UserInfo(String fullName, String avatarUrl, Integer rank, LeaderboardUserResult leaderboardUserResult) {}
}
