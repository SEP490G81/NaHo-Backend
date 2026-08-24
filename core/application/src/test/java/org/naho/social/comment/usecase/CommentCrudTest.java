package org.naho.social.comment.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.learning.port.out.LearningPathNodeRepositoryPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.EventPublisherPort;
import org.naho.social.comment.command.CommentCreateCommand;
import org.naho.social.comment.command.CommentDeleteCommand;
import org.naho.social.comment.command.CommentReadCommand;
import org.naho.social.comment.command.CommentUpdateCommand;
import org.naho.social.comment.exception.CommentErrorCode;
import org.naho.social.comment.mapper.CommentDomainMapper;
import org.naho.social.comment.mapper.CommentListResultMapper;
import org.naho.social.comment.mapper.CommentResultMapper;
import org.naho.social.comment.model.Comment;
import org.naho.social.comment.port.out.CommentRepositoryPort;
import org.naho.social.comment.result.CommentListResult;
import org.naho.social.comment.result.CommentResult;
import org.naho.user.port.out.UserRepositoryPort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentCrudTest {

    @Mock
    private CommentRepositoryPort commentRepositoryPort;
    @Mock
    private CommentListResultMapper commentListResultMapper;
    @Mock
    private CommentDomainMapper commentDomainMapper;
    @Mock
    private CommentResultMapper commentResultMapper;
    @Mock
    private EventPublisherPort eventPublisherPort;
    @Mock
    private UserRepositoryPort userRepositoryPort;
    @Mock
    private LearningPathNodeRepositoryPort learningPathNodeRepositoryPort;

    @InjectMocks
    private CommentCrudUseCase commentCrudUseCase;

    @Test
    @DisplayName("UTCID01 - Lấy danh sách bình luận theo questionId thành công")
    void UTCID01_GetListCommentOfQuestion_Success() {
        CommentReadCommand command = new CommentReadCommand(10L);
        Comment comment = mock(Comment.class);
        CommentListResult listResult = mock(CommentListResult.class);

        when(commentRepositoryPort.getListCommentByQuestionId(10L)).thenReturn(List.of(comment));
        when(commentListResultMapper.domainToResult(10L, List.of(comment), 1L)).thenReturn(listResult);

        CommentListResult result = commentCrudUseCase.getListCommentOfQuestion(command, 1L);

        assertNotNull(result);
    }

    @Test
    @DisplayName("UTCID02 - Tạo mới bình luận thành công và không gửi event khi không có parent")
    void UTCID02_CreateComment_Success() {
        CommentCreateCommand command = new CommentCreateCommand(1L, 10L, "Hello", null);
        Comment comment = Comment.builder().id(100L).userId(1L).questionId(10L).content("Hello").build();
        CommentResult commentResult = mock(CommentResult.class);

        when(commentDomainMapper.commandToModel(command)).thenReturn(comment);
        when(commentRepositoryPort.save(comment)).thenReturn(comment);
        when(commentResultMapper.domainToResult(comment)).thenReturn(commentResult);

        CommentResult result = commentCrudUseCase.createComment(command);

        assertNotNull(result);
        verify(eventPublisherPort, never()).publish(any());
    }

    @Test
    @DisplayName("UTCID03 - Cập nhật bình luận thất bại khi không phải chủ sở hữu")
    void UTCID03_UpdateComment_NotOwner() {
        CommentUpdateCommand command = new CommentUpdateCommand(100L, 99L, "New Content");
        Comment existing = Comment.builder().id(100L).userId(1L).questionId(10L).content("Hello").build();

        when(commentRepositoryPort.findByCommentId(100L)).thenReturn(Optional.of(existing));

        ApplicationException ex = assertThrows(ApplicationException.class, () -> commentCrudUseCase.updateComment(command));
        assertEquals(CommentErrorCode.COMMENT_NOT_AUTHORIZED, ex.getErrorCode());
    }

    @Test
    @DisplayName("UTCID04 - Xóa bình luận thành công khi là Admin")
    void UTCID04_DeleteComment_AdminSuccess() {
        CommentDeleteCommand command = new CommentDeleteCommand(100L, 99L, true);
        Comment comment = Comment.builder().id(100L).userId(1L).questionId(10L).content("Hello").build();

        when(commentRepositoryPort.findByCommentId(100L)).thenReturn(Optional.of(comment));

        commentCrudUseCase.deleteComment(command);

        verify(commentRepositoryPort, times(1)).delete(comment);
    }
}
