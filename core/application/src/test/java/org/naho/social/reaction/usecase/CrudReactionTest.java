package org.naho.social.reaction.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.learning.port.out.LearningPathNodeRepositoryPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.EventPublisherPort;
import org.naho.social.comment.exception.CommentErrorCode;
import org.naho.social.comment.model.Comment;
import org.naho.social.comment.port.out.CommentRepositoryPort;
import org.naho.social.reaction.command.ReactionActionCommand;
import org.naho.social.reaction.mapper.ReactionActionCommandMapper;
import org.naho.social.reaction.mapper.ReactionResultResponseMapper;
import org.naho.social.reaction.model.Reaction;
import org.naho.social.reaction.port.out.ReactionRepositoryPort;
import org.naho.social.reaction.result.ReactionResult;
import org.naho.social.reaction.type.ReactionAction;
import org.naho.social.reaction.type.ReactionType;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.model.User;
import org.naho.user.port.out.UserRepositoryPort;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrudReactionTest {

    @Mock
    private ReactionRepositoryPort reactionRepositoryPort;
    @Mock
    private ReactionActionCommandMapper reactionActionCommandMapper;
    @Mock
    private ReactionResultResponseMapper reactionResultResponseMapper;
    @Mock
    private UserRepositoryPort userRepositoryPort;
    @Mock
    private CommentRepositoryPort commentRepositoryPort;
    @Mock
    private EventPublisherPort eventPublisherPort;
    @Mock
    private LearningPathNodeRepositoryPort learningPathNodeRepositoryPort;

    @InjectMocks
    private CrudReactionUsecase crudReactionUsecase;

    @Test
    @DisplayName("UTCID01 - Thêm mới Reaction cho Comment thành công")
    void UTCID01_ChooseReaction_AddSuccess() {
        // comment_id = 10L, userId = 1L
        ReactionActionCommand command = new ReactionActionCommand(10L, 1L, ReactionType.LIKE);
        User user = User.builder().id(1L).fullName("Nguyen Van A").build();
        Comment comment = mock(Comment.class);
        Reaction newReaction = mock(Reaction.class);
        ReactionResult reactionResult = mock(ReactionResult.class);

        when(comment.getUserId()).thenReturn(2L);
        when(comment.getId()).thenReturn(10L);
        when(comment.getQuestionId()).thenReturn(100L);

        when(userRepositoryPort.findById(1L)).thenReturn(Optional.of(user));
        when(commentRepositoryPort.findByCommentId(10L)).thenReturn(Optional.of(comment));
        when(reactionRepositoryPort.findByUserAndTarget(1L, 10L)).thenReturn(null);
        when(reactionActionCommandMapper.commandToDomain(command)).thenReturn(newReaction);
        when(reactionResultResponseMapper.domainToResult(newReaction, ReactionAction.ADDED, "Nguyen Van A")).thenReturn(reactionResult);

        ReactionResult result = crudReactionUsecase.chooseReaction(command);

        assertNotNull(result);
        verify(reactionRepositoryPort, times(1)).save(newReaction);
        verify(eventPublisherPort, times(1)).publish(any());
    }

    @Test
    @DisplayName("UTCID02 - Thất bại khi không tìm thấy User")
    void UTCID02_ChooseReaction_UserNotFound() {
        // comment_id = 10L, userId = 99L
        ReactionActionCommand command = new ReactionActionCommand(10L, 99L, ReactionType.LIKE);
        when(userRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> crudReactionUsecase.chooseReaction(command));
        assertEquals(UserErrorCode.USER_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("UTCID03 - Thất bại khi không tìm thấy Comment")
    void UTCID03_ChooseReaction_CommentNotFound() {
        // comment_id = 99L, userId = 1L
        ReactionActionCommand command = new ReactionActionCommand(99L, 1L, ReactionType.LIKE);
        User user = User.builder().id(1L).fullName("User").build();

        when(userRepositoryPort.findById(1L)).thenReturn(Optional.of(user));
        when(commentRepositoryPort.findByCommentId(99L)).thenReturn(Optional.empty());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> crudReactionUsecase.chooseReaction(command));
        assertEquals(CommentErrorCode.COMMENT_NOT_FOUND, ex.getErrorCode());
    }
}
