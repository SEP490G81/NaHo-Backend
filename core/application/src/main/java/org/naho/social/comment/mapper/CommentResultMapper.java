package org.naho.social.comment.mapper;

import org.naho.social.comment.model.Comment;
import org.naho.social.comment.result.CommentResonseResult;
import org.naho.social.reaction.model.Reaction;
import org.naho.social.reaction.type.ReactionType;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommentResultMapper {

    public CommentResonseResult domainToResult(Comment comment) {
        return new CommentResonseResult(
                comment.getId(),
                comment.getQuestionId(),
                comment.getUserId(),
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

        return new CommentResonseResult(
                comment.getId(),
                comment.getQuestionId(),
                comment.getUserId(),
                comment.getParentId(),
                comment.getContent(),
                toLocalDateTime(comment),
                toLocalDateTimeModified(comment),
                summary,
                null
        );
    }

    private LocalDateTime toLocalDateTime(Comment comment) {
        if (comment.getCreatedTime() == null) return null;
        return LocalDateTime.ofInstant(comment.getCreatedTime(), ZoneId.systemDefault());
    }

    private LocalDateTime toLocalDateTimeModified(Comment comment) {
        if (comment.getModifiedTime() == null) return null;
        return LocalDateTime.ofInstant(comment.getModifiedTime(), ZoneId.systemDefault());
    }
}
