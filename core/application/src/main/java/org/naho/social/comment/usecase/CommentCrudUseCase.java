package org.naho.social.comment.usecase;

import org.naho.i18n.message.social.CommentDetailMessageKey;
import org.naho.learning.port.out.LearningPathNodeRepositoryPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.EventPublisherPort;
import org.naho.social.comment.command.CommentCreateCommand;
import org.naho.social.comment.command.CommentDeleteCommand;
import org.naho.social.comment.command.CommentReadCommand;
import org.naho.social.comment.command.CommentUpdateCommand;
import org.naho.social.comment.event.CommentRepliedEvent;
import org.naho.social.comment.exception.CommentErrorCode;
import org.naho.social.comment.mapper.CommentDomainMapper;
import org.naho.social.comment.mapper.CommentListResultMapper;
import org.naho.social.comment.mapper.CommentResultMapper;
import org.naho.social.comment.model.Comment;
import org.naho.social.comment.port.in.CommentCrudInputPort;
import org.naho.social.comment.port.out.CommentRepositoryPort;
import org.naho.social.comment.result.CommentListResult;
import org.naho.social.comment.result.CommentResult;
import org.naho.user.port.out.UserRepositoryPort;

import java.util.List;

public class CommentCrudUseCase implements CommentCrudInputPort {
    private final CommentRepositoryPort commentRepositoryPort;
    private final CommentListResultMapper commentListResultMapper;
    private final CommentDomainMapper commentDomainMapper;
    private final CommentResultMapper commentResultMapper;
    private final EventPublisherPort eventPublisherPort;
    private final UserRepositoryPort userRepositoryPort;
    private final LearningPathNodeRepositoryPort learningPathNodeRepositoryPort;

    public CommentCrudUseCase(
            CommentRepositoryPort commentRepositoryPort,
            CommentListResultMapper commentListResultMapper,
            CommentDomainMapper commentDomainMapper,
            CommentResultMapper commentResultMapper,
            EventPublisherPort eventPublisherPort,
            UserRepositoryPort userRepositoryPort,
            LearningPathNodeRepositoryPort learningPathNodeRepositoryPort
    ) {
        this.commentRepositoryPort = commentRepositoryPort;
        this.commentListResultMapper = commentListResultMapper;
        this.commentDomainMapper = commentDomainMapper;
        this.commentResultMapper = commentResultMapper;
        this.eventPublisherPort = eventPublisherPort;
        this.userRepositoryPort = userRepositoryPort;
        this.learningPathNodeRepositoryPort = learningPathNodeRepositoryPort;
    }

    @Override
    public CommentListResult getListCommentOfQuestion(CommentReadCommand command, Long currentUserId) {
        List<Comment> listComment = commentRepositoryPort.getListCommentByQuestionId(command.speakingQuestionId());
        return commentListResultMapper.domainToResult(command.speakingQuestionId(), listComment, currentUserId);
    }

    @Override
    public CommentResult createComment(CommentCreateCommand command) {
        Comment parentComment = null;
        if (command.parentId() != null) {
            parentComment = commentRepositoryPort.findByCommentId(command.parentId())
                    .orElseThrow(() -> new ApplicationException(
                            CommentErrorCode.COMMENT_NOT_FOUND,
                            CommentDetailMessageKey.COMMENT_ID_NOT_FOUND,
                            command.parentId()
                    ));
        }

        Comment newComment = commentDomainMapper.commandToModel(command);
        Comment commentSaved = commentRepositoryPort.save(newComment);

        if (parentComment != null && !parentComment.getUserId().equals(commentSaved.getUserId())) {
            eventPublisherPort.publish(new CommentRepliedEvent(
                    parentComment.getUserId(),
                    commentSaved.getUserId(),
                    commentSaved.getQuestionId(),
                    commentSaved.getId()
            ));
        }

        return commentResultMapper.domainToResult(commentSaved);
    }

    @Override
    public CommentResult updateComment(CommentUpdateCommand command) {
        Comment existing = commentRepositoryPort.findByCommentId(command.commentId())
                .orElseThrow(() -> new ApplicationException(
                        CommentErrorCode.COMMENT_NOT_FOUND,
                        CommentDetailMessageKey.COMMENT_ID_NOT_FOUND,
                        command.commentId()
                ));

        boolean isOwner = command.userId() != null && command.userId().equals(existing.getUserId());
        if (!isOwner) {
            throw new ApplicationException(
                    CommentErrorCode.COMMENT_NOT_AUTHORIZED,
                    CommentDetailMessageKey.COMMENT_DELETE_FORBIDDEN
            );
        }

        Comment commentToSave = Comment.builder()
                .id(existing.getId())
                .userId(existing.getUserId())
                .questionId(existing.getQuestionId())
                .parentId(existing.getParentId())
                .content(command.newContent() != null ? command.newContent() : existing.getContent())
                .createdTime(existing.getCreatedTime())
                .build();

        Comment commentUpdated = commentRepositoryPort.save(commentToSave);
        return commentResultMapper.domainToResult(commentUpdated);
    }

    @Override
    public void deleteComment(CommentDeleteCommand command) {
        Comment comment = commentRepositoryPort.findByCommentId(command.commentId())
                .orElseThrow(() -> new ApplicationException(
                        CommentErrorCode.COMMENT_NOT_FOUND,
                        CommentDetailMessageKey.COMMENT_ID_NOT_FOUND,
                        command.commentId()
                ));

        boolean isOwner = command.userId() != null && command.userId().equals(comment.getUserId());
        if (!isOwner && !command.isAdmin()) {
            throw new ApplicationException(
                    CommentErrorCode.COMMENT_NOT_AUTHORIZED,
                    CommentDetailMessageKey.COMMENT_DELETE_FORBIDDEN
            );
        }

        commentRepositoryPort.delete(comment);
    }
}
