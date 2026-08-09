package org.naho.social.comment.mapper;

import org.naho.file.port.in.CrudFileInputPort;
import org.naho.file.result.FileResult;
import org.naho.social.comment.model.Comment;
import org.naho.social.comment.result.CommentResonseResult;
import org.naho.social.reaction.model.Reaction;
import org.naho.social.reaction.type.ReactionType;
import org.naho.user.model.User;
import org.naho.user.port.in.CrudAuthProviderInputPort;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.result.AuthProviderResult;

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

    public CommentResonseResult domainToResult(Comment comment) {
        UserInfo userInfo = fetchUserInfo(comment.getUserId());
        return new CommentResonseResult(
                comment.getId(),
                comment.getQuestionId(),
                comment.getUserId(),
                userInfo.username(),
                userInfo.avatarUrl(),
                comment.getParentId(),
                comment.getContent(),
                toLocalDateTime(comment),
                toLocalDateTimeModified(comment),
                null,
                null
        );
    }

    public CommentResonseResult domainToResultWithReactions(
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

        CommentResonseResult.ReactionSummary summary = new CommentResonseResult.ReactionSummary(
                reactions.size(),
                counts,
                myReaction
        );

        UserInfo userInfo = fetchUserInfo(comment.getUserId());

        return new CommentResonseResult(
                comment.getId(),
                comment.getQuestionId(),
                comment.getUserId(),
                userInfo.username(),
                userInfo.avatarUrl(),
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
            return new UserInfo(null, null);
        }
        Optional<User> userOpt = userRepositoryPort.findById(userId);
        if (userOpt.isEmpty()) {
            return new UserInfo(null, null);
        }
        User user = userOpt.get();
        String username = user.getUsername() != null ? user.getUsername().getValue() : user.getFullName();

        String avatarUrl = null;
        if (user.getAvatarFileId() != null && crudFileInputPort != null) {
            try {
                FileResult fileResult = crudFileInputPort.findById(user.getAvatarFileId());
                if (fileResult != null) {
                    avatarUrl = fileResult.getAccessUrl();
                }
            } catch (Exception ignored) {
            }
        }
        if (avatarUrl == null && crudAuthProviderInputPort != null) {
            try {
                List<AuthProviderResult> providers = crudAuthProviderInputPort.findAllByUser_Id(userId);
                if (providers != null && !providers.isEmpty()) {
                    avatarUrl = providers.get(0).avatarUrl();
                }
            } catch (Exception ignored) {
            }
        }
        return new UserInfo(username, avatarUrl);
    }

    private LocalDateTime toLocalDateTime(Comment comment) {
        if (comment.getCreatedTime() == null) return null;
        return LocalDateTime.ofInstant(comment.getCreatedTime(), ZoneId.systemDefault());
    }

    private LocalDateTime toLocalDateTimeModified(Comment comment) {
        if (comment.getModifiedTime() == null) return null;
        return LocalDateTime.ofInstant(comment.getModifiedTime(), ZoneId.systemDefault());
    }

    private record UserInfo(String username, String avatarUrl) {
    }
}
