package org.naho.social.reaction.usecase;

import org.naho.i18n.message.social.CommentDetailMessageKey;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.notification.event.SendNotificationEvent;
import org.naho.notification.type.NotificationType;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.EventPublisherPort;
import org.naho.social.comment.exception.CommentErrorCode;
import org.naho.social.comment.model.Comment;
import org.naho.social.comment.port.out.CommentRepositoryPort;
import org.naho.social.reaction.command.ReactionActionCommand;
import org.naho.social.reaction.mapper.ReactionActionCommandMapper;
import org.naho.social.reaction.mapper.ReactionResultResponseMapper;
import org.naho.social.reaction.model.Reaction;
import org.naho.social.reaction.port.in.CrudReactionTypeInputPort;
import org.naho.social.reaction.port.out.ReactionRepositoryPort;
import org.naho.social.reaction.result.ReactionDetailResult;
import org.naho.social.reaction.result.ReactionResult;
import org.naho.social.reaction.type.ReactionAction;
import org.naho.social.reaction.type.ReactionType;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.port.out.UserRepositoryPort;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CrudReactionUsecase implements CrudReactionTypeInputPort {
    private final ReactionRepositoryPort reactionRepositoryPort;
    private final ReactionActionCommandMapper reactionActionCommandMapper;
    private final ReactionResultResponseMapper reactionResultResponseMapper;
    private final UserRepositoryPort userRepositoryPort;
    private final CommentRepositoryPort commentRepositoryPort;
    private final EventPublisherPort eventPublisherPort;
    private final org.naho.learning.port.out.LearningPathNodeRepositoryPort learningPathNodeRepositoryPort;

    public CrudReactionUsecase(
            ReactionRepositoryPort reactionRepositoryPort,
            ReactionActionCommandMapper reactionActionCommandMapper,
            ReactionResultResponseMapper reactionResultResponseMapper,
            UserRepositoryPort userRepositoryPort,
            CommentRepositoryPort commentRepositoryPort,
            EventPublisherPort eventPublisherPort,
            org.naho.learning.port.out.LearningPathNodeRepositoryPort learningPathNodeRepositoryPort
    ) {
        this.reactionRepositoryPort = reactionRepositoryPort;
        this.reactionActionCommandMapper = reactionActionCommandMapper;
        this.reactionResultResponseMapper = reactionResultResponseMapper;
        this.userRepositoryPort = userRepositoryPort;
        this.commentRepositoryPort = commentRepositoryPort;
        this.eventPublisherPort = eventPublisherPort;
        this.learningPathNodeRepositoryPort = learningPathNodeRepositoryPort;
    }

    @Override
    public ReactionResult chooseReaction(ReactionActionCommand reactionActionCommand) {
        String username = userRepositoryPort.findById(reactionActionCommand.userId())
                .orElseThrow(() -> new ApplicationException(
                        UserErrorCode.USER_NOT_FOUND,
                        UserDetailMessageKey.USER_ID_NOT_FOUND
                ))
                .getUsername()
                .getValue();

        Comment comment = commentRepositoryPort.findByCommentId(reactionActionCommand.comment_id())
                .orElseThrow(() -> new ApplicationException(
                        CommentErrorCode.COMMENT_NOT_FOUND,
                        CommentDetailMessageKey.COMMENT_ID_NOT_FOUND,
                        reactionActionCommand.comment_id()
                ));

        Reaction existingReaction = reactionRepositoryPort.findByUserAndTarget(
                reactionActionCommand.userId(),
                reactionActionCommand.comment_id()
        );

        if (existingReaction == null) {
            Reaction newReaction = reactionActionCommandMapper.commandToDomain(reactionActionCommand);
            reactionRepositoryPort.save(newReaction);

            // Notify comment owner
            if (!comment.getUserId().equals(reactionActionCommand.userId())) {
                String metadata = "{\"questionId\": " + comment.getQuestionId() + ", \"commentId\": " + comment.getId() + "}";
                String targetUrl = learningPathNodeRepositoryPort.getFrontendUrlPath(comment.getQuestionId())
                        .map(path -> path + "#comment-" + comment.getId())
                        .orElse("/speaking-questions/" + comment.getQuestionId() + "#comment-" + comment.getId());

                eventPublisherPort.publish(new SendNotificationEvent(
                        this,
                        comment.getUserId(),
                        NotificationType.SOCIAL,
                        "Có người thích bình luận của bạn",
                        username + " vừa thả cảm xúc vào bình luận của bạn.",
                        targetUrl,
                        metadata
                ));
            }

            return reactionResultResponseMapper.domainToResult(newReaction, ReactionAction.ADDED, username);
        }

        if (existingReaction.getReactionType() == reactionActionCommand.reactionType()) {
            reactionRepositoryPort.delete(existingReaction.getId());
            return new ReactionResult(reactionActionCommand.comment_id(), reactionActionCommand.reactionType(), ReactionAction.REMOVED, username);
        }

        Reaction updatedReaction = existingReaction.toBuilder()
                .reactionType(reactionActionCommand.reactionType())
                .build();
        reactionRepositoryPort.save(updatedReaction);
        return reactionResultResponseMapper.domainToResult(updatedReaction, ReactionAction.UPDATED, username);
    }

    @Override
    public ReactionDetailResult getReactionsByComment(Long commentId) {
        if (commentRepositoryPort.findByCommentId(commentId).isEmpty()) {
            throw new ApplicationException(
                    CommentErrorCode.COMMENT_NOT_FOUND,
                    CommentDetailMessageKey.COMMENT_ID_NOT_FOUND,
                    commentId
            );
        }

        List<Reaction> reactions = reactionRepositoryPort.findByCommentId(commentId);

        Map<ReactionType, Long> counts = reactions.stream()
                .collect(Collectors.groupingBy(Reaction::getReactionType, Collectors.counting()));

        Map<ReactionType, List<ReactionDetailResult.ReactionUserItem>> users = reactions.stream()
                .collect(Collectors.groupingBy(
                        Reaction::getReactionType,
                        Collectors.mapping(
                                r -> {
                                    String fullName = userRepositoryPort.findById(r.getUserId())
                                            .map(u -> u.getFullName() != null && !u.getFullName().isBlank()
                                                    ? u.getFullName()
                                                    : u.getUsername().getValue())
                                            .orElse("Một người dùng");
                                    return new ReactionDetailResult.ReactionUserItem(r.getUserId(), fullName);
                                },
                                Collectors.toList()
                        )
                ));

        return new ReactionDetailResult(commentId, reactions.size(), counts, users);
    }
}
