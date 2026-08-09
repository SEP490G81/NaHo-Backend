package org.naho.social.comment.usecase;

import org.naho.i18n.message.social.CommentDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.social.comment.command.CommentCreateCommand;
import org.naho.social.comment.command.CommentDeleteCommand;
import org.naho.social.comment.command.CommentReadCommand;
import org.naho.social.comment.command.CommentUpdateCommand;
import org.naho.social.comment.exception.CommentErrorCode;
import org.naho.social.comment.mapper.CommentDomainMapper;
import org.naho.social.comment.mapper.CommentListResultMapper;
import org.naho.social.comment.mapper.CommentResultMapper;
import org.naho.social.comment.model.Comment;
import org.naho.social.comment.port.in.CommentCrudInputPort;
import org.naho.social.comment.port.out.CommentRepositoryPort;
import org.naho.social.comment.result.CommentListResponseResult;
import org.naho.social.comment.result.CommentResonseResult;

import java.util.List;
import java.util.Optional;

public class CommentCrudUsecase implements CommentCrudInputPort {
    private final CommentRepositoryPort commentRepositoryPort;
    private final CommentListResultMapper commentListResultMapper;
    private final CommentDomainMapper commentDomainMapper;
    private final CommentResultMapper commentResultMapper;

    public CommentCrudUsecase(CommentRepositoryPort commentRepositoryPort,
                               CommentListResultMapper commentListResultMapper,
                               CommentDomainMapper commentDomainMapper,
                               CommentResultMapper commentResultMapper) {
        this.commentRepositoryPort = commentRepositoryPort;
        this.commentListResultMapper = commentListResultMapper;
        this.commentDomainMapper = commentDomainMapper;
        this.commentResultMapper = commentResultMapper;
    }

    @Override
    public CommentListResponseResult getListCommentOfQuestion(CommentReadCommand command, Long currentUserId) {
        List<Comment> listComment = commentRepositoryPort.getListCommentByQuestionId(command.speakingQuestionId());
        return commentListResultMapper.domainToResult(command.speakingQuestionId(), listComment, currentUserId);
    }

    @Override
    public CommentResonseResult createComment(CommentCreateCommand command) {
        Comment newComment = commentDomainMapper.commandToModel(command);
        Comment commentSaved = commentRepositoryPort.save(newComment);
        return commentResultMapper.domainToResult(commentSaved);
    }

    @Override
    public CommentResonseResult updateComment(CommentUpdateCommand command) {
        Optional<Comment> commentOpt = commentRepositoryPort.findByCommentId(command.commentId());
        if (commentOpt.isEmpty()) {
            throw new ApplicationException(
                    CommentErrorCode.COMMENT_NOT_FOUND,
                    CommentDetailMessageKey.COMMENT_ID_NOT_FOUND,
                    command.commentId()
            );
        }
        Comment existing = commentOpt.get();
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
    public CommentResonseResult deleteComment(CommentDeleteCommand command) {
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
        return commentResultMapper.domainToResult(comment);
    }
}
