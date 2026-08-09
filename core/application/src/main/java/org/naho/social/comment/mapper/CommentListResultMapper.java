package org.naho.social.comment.mapper;

import org.naho.social.comment.model.Comment;
import org.naho.social.comment.result.CommentListResponseResult;
import org.naho.social.comment.result.CommentResonseResult;
import org.naho.social.reaction.model.Reaction;
import org.naho.social.reaction.port.out.ReactionRepositoryPort;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommentListResultMapper {

    private final CommentResultMapper commentResultMapper;
    private final ReactionRepositoryPort reactionRepositoryPort;

    public CommentListResultMapper(CommentResultMapper commentResultMapper,
                                   ReactionRepositoryPort reactionRepositoryPort) {
        this.commentResultMapper = commentResultMapper;
        this.reactionRepositoryPort = reactionRepositoryPort;
    }

    public CommentListResponseResult domainToResult(Long questionId, List<Comment> allComments, Long currentUserId) {
        Map<Long, List<Comment>> childrenByParentId = allComments.stream()
                .filter(c -> c.getParentId() != null)
                .collect(Collectors.groupingBy(Comment::getParentId));

        List<CommentResonseResult> roots = allComments.stream()
                .filter(c -> c.getParentId() == null)
                .map(parent -> buildWithChildren(parent, childrenByParentId, currentUserId))
                .toList();

        return new CommentListResponseResult(questionId, roots);
    }

    private CommentResonseResult buildWithChildren(
            Comment comment,
            Map<Long, List<Comment>> childrenByParentId,
            Long currentUserId
    ) {
        List<Reaction> reactions = reactionRepositoryPort.findByCommentId(comment.getId());
        CommentResonseResult result = commentResultMapper.domainToResultWithReactions(comment, reactions, currentUserId);

        List<Comment> children = childrenByParentId.getOrDefault(comment.getId(), new ArrayList<>());
        List<CommentResonseResult> childResults = children.stream()
                .map(child -> buildWithChildren(child, childrenByParentId, currentUserId))
                .toList();

        return new CommentResonseResult(
                result.commentId(),
                result.questionId(),
                result.userId(),
                result.username(),
                result.avatarUrl(),
                result.parentId(),
                result.content(),
                result.createdTime(),
                result.modifiedTime(),
                result.reactionSummary(),
                childResults
        );
    }
}
