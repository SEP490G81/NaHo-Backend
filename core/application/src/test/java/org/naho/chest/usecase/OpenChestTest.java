package org.naho.chest.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.chest.command.OpenChestCommand;
import org.naho.chest.exception.ChestErrorCode;
import org.naho.chest.model.Chest;
import org.naho.chest.port.out.ChestRepositoryPort;
import org.naho.chest.result.OpenChestResult;
import org.naho.i18n.message.chest.ChestDetailMessageKey;
import org.naho.i18n.message.learning.LearningPathNodeDetailMessageKey;
import org.naho.i18n.message.learning.UserLearningProgressDetailMessageKey;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.learning.exception.LearningPathNodeErrorCode;
import org.naho.learning.exception.UserLearningProgressErrorCode;
import org.naho.learning.model.LearningPathNode;
import org.naho.learning.model.UserLearningProgress;
import org.naho.learning.port.in.CrudUserLearningProgressInputPort;
import org.naho.learning.port.in.UserLearningStreakInputPort;
import org.naho.learning.port.out.LearningPathNodeRepositoryPort;
import org.naho.learning.port.out.UserLearningProgressRepositoryPort;
import org.naho.learning.port.out.UserNodeProgressRepositoryPort;
import org.naho.learning.type.NodeType;
import org.naho.point.port.in.CrudPointHistoryInputPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.model.User;
import org.naho.user.port.out.UserRepositoryPort;

import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpenChestTest {

    @Mock
    private ChestRepositoryPort chestRepositoryPort;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private CrudPointHistoryInputPort crudPointHistoryInputPort;

    @Mock
    private UserLearningProgressRepositoryPort userLearningProgressRepositoryPort;

    @Mock
    private TransactionPort transactionPort;

    @Mock
    private LearningPathNodeRepositoryPort learningPathNodeRepositoryPort;

    @Mock
    private UserNodeProgressRepositoryPort userNodeProgressRepositoryPort;

    @Mock
    private UserLearningStreakInputPort userLearningStreakInputPort;

    @Mock
    private CrudUserLearningProgressInputPort crudUserLearningProgressInputPort;

    @InjectMocks
    private OpenChestUseCase openChestUseCase;

    @BeforeEach
    void setUp() {
        lenient().when(transactionPort.execute(any(Supplier.class))).thenAnswer(invocation -> {
            Supplier<?> supplier = invocation.getArgument(0);
            return supplier.get();
        });
    }

    @Test
    @DisplayName("UTCID01 - Mở rương báu thành công khi command hợp lệ")
    void UTCID01_OpenChestSuccess() {
        // Arrange
        Long node = 100L;
        Long userId = 1L;
        Long chestId = 10L;

        OpenChestCommand command = new OpenChestCommand(node, userId);

        LearningPathNode chestNode = LearningPathNode.builder()
                .id(node)
                .objectiveId(1L)
                .chestId(chestId)
                .globalOrderIndex(1.0)
                .orderIndex(1.0)
                .nodeType(NodeType.CHEST)
                .build();

        UserLearningProgress progress = mock(UserLearningProgress.class);
        when(progress.getFarthestAvailableNodeGlobalOrderIndex()).thenReturn(5.0);

        Chest chest = mock(Chest.class);
        when(chest.getRandomPoint()).thenReturn(50);

        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);

        when(learningPathNodeRepositoryPort.findByIdAndNodeType(node, NodeType.CHEST))
                .thenReturn(Optional.of(chestNode));
        when(userNodeProgressRepositoryPort.existsByLearningPathNodeIdAndUserId(node, userId))
                .thenReturn(false);
        when(userLearningProgressRepositoryPort.findByUserId(userId))
                .thenReturn(Optional.of(progress));
        when(chestRepositoryPort.findById(chestId))
                .thenReturn(Optional.of(chest));
        when(userRepositoryPort.findById(userId))
                .thenReturn(Optional.of(user));
        when(crudUserLearningProgressInputPort.updateFarthestAvailableNodeWhenCompletedANode(any()))
                .thenReturn(progress);
        when(userLearningStreakInputPort.updateUserLearningStreak(any()))
                .thenReturn(progress);

        // Act
        OpenChestResult result = openChestUseCase.openChest(command);

        // Assert
        assertNotNull(result);
        assertEquals(50.0, result.earnedPoint());

        verify(progress, times(1)).addPoint(50.0);
        verify(progress, times(1)).setLastLearningNodeId(node);
        verify(progress, times(1)).setLastLearningNodeGlobalOrderIndex(1.0);
        verify(userLearningProgressRepositoryPort, times(1)).save(progress);
        verify(userNodeProgressRepositoryPort, times(1)).save(any());
        verify(crudPointHistoryInputPort, times(1)).createPointHistory(any());
    }

    @Test
    @DisplayName("UTCID02 - Mở rương báu thất bại khi không tìm thấy nút đường dẫn học tập")
    void UTCID02_LearningPathNodeNotFound() {
        // Arrange
        OpenChestCommand command = new OpenChestCommand(999L, 1L);

        when(learningPathNodeRepositoryPort.findByIdAndNodeType(999L, NodeType.CHEST))
                .thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> openChestUseCase.openChest(command)
        );

        assertEquals(LearningPathNodeErrorCode.LEARNING_PATH_NODE_NOT_FOUND, exception.getErrorCode());
        assertEquals(LearningPathNodeDetailMessageKey.LEARNING_PATH_NODE_ID_NOT_FOUND, exception.getMessage());
        verify(userNodeProgressRepositoryPort, never()).existsByLearningPathNodeIdAndUserId(any(), any());
    }

    @Test
    @DisplayName("UTCID03 - Mở rương báu thất bại khi rương báu đã được mở trước đó")
    void UTCID03_ChestAlreadyOpened() {
        // Arrange
        Long node = 100L;
        Long userId = 1L;
        OpenChestCommand command = new OpenChestCommand(node, userId);

        LearningPathNode chestNode = LearningPathNode.builder()
                .id(node)
                .objectiveId(1L)
                .chestId(10L)
                .globalOrderIndex(1.0)
                .orderIndex(1.0)
                .nodeType(NodeType.CHEST)
                .build();

        when(learningPathNodeRepositoryPort.findByIdAndNodeType(node, NodeType.CHEST))
                .thenReturn(Optional.of(chestNode));
        when(userNodeProgressRepositoryPort.existsByLearningPathNodeIdAndUserId(node, userId))
                .thenReturn(true);

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> openChestUseCase.openChest(command)
        );

        assertEquals(ChestErrorCode.CHEST_ALREADY_OPENED, exception.getErrorCode());
        assertEquals(ChestDetailMessageKey.CHEST_ALREADY_OPENED, exception.getMessage());
        verify(userLearningProgressRepositoryPort, never()).findByUserId(any());
    }

    @Test
    @DisplayName("UTCID04 - Mở rương báu thất bại khi không tìm thấy tiến trình học tập của người dùng")
    void UTCID04_UserLearningProgressNotFound() {
        // Arrange
        Long node = 100L;
        Long userId = 1L;
        OpenChestCommand command = new OpenChestCommand(node, userId);

        LearningPathNode chestNode = LearningPathNode.builder()
                .id(node)
                .objectiveId(1L)
                .chestId(10L)
                .globalOrderIndex(1.0)
                .orderIndex(1.0)
                .nodeType(NodeType.CHEST)
                .build();

        when(learningPathNodeRepositoryPort.findByIdAndNodeType(node, NodeType.CHEST))
                .thenReturn(Optional.of(chestNode));
        when(userNodeProgressRepositoryPort.existsByLearningPathNodeIdAndUserId(node, userId))
                .thenReturn(false);
        when(userLearningProgressRepositoryPort.findByUserId(userId))
                .thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> openChestUseCase.openChest(command)
        );

        assertEquals(UserLearningProgressErrorCode.USER_LEARNING_PROGRESS_NOT_FOUND, exception.getErrorCode());
        assertEquals(UserLearningProgressDetailMessageKey.USER_LEARNING_PROGRESS_NOT_FOUND_BY_USER_ID, exception.getMessage());
        verify(chestRepositoryPort, never()).findById(any());
    }

    @Test
    @DisplayName("UTCID05 - Mở rương báu thất bại khi rương báu chưa được mở khóa")
    void UTCID05_ChestLocked() {
        // Arrange
        Long node = 100L;
        Long userId = 1L;
        OpenChestCommand command = new OpenChestCommand(node, userId);

        LearningPathNode chestNode = LearningPathNode.builder()
                .id(node)
                .objectiveId(1L)
                .chestId(10L)
                .globalOrderIndex(10.0)
                .orderIndex(10.0)
                .nodeType(NodeType.CHEST)
                .build();

        UserLearningProgress progress = mock(UserLearningProgress.class);
        when(progress.getFarthestAvailableNodeGlobalOrderIndex()).thenReturn(5.0);

        when(learningPathNodeRepositoryPort.findByIdAndNodeType(node, NodeType.CHEST))
                .thenReturn(Optional.of(chestNode));
        when(userNodeProgressRepositoryPort.existsByLearningPathNodeIdAndUserId(node, userId))
                .thenReturn(false);
        when(userLearningProgressRepositoryPort.findByUserId(userId))
                .thenReturn(Optional.of(progress));

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> openChestUseCase.openChest(command)
        );

        assertEquals(ChestErrorCode.CHEST_LOCKED, exception.getErrorCode());
        assertEquals(ChestDetailMessageKey.CHEST_LOCKED, exception.getMessage());
        verify(chestRepositoryPort, never()).findById(any());
    }

    @Test
    @DisplayName("UTCID06 - Mở rương báu thất bại khi không tìm thấy thông tin rương báu")
    void UTCID06_ChestNotFound() {
        // Arrange
        Long node = 100L;
        Long userId = 1L;
        Long chestId = 10L;

        OpenChestCommand command = new OpenChestCommand(node, userId);

        LearningPathNode chestNode = LearningPathNode.builder()
                .id(node)
                .objectiveId(1L)
                .chestId(chestId)
                .globalOrderIndex(1.0)
                .orderIndex(1.0)
                .nodeType(NodeType.CHEST)
                .build();

        UserLearningProgress progress = mock(UserLearningProgress.class);
        when(progress.getFarthestAvailableNodeGlobalOrderIndex()).thenReturn(5.0);

        when(learningPathNodeRepositoryPort.findByIdAndNodeType(node, NodeType.CHEST))
                .thenReturn(Optional.of(chestNode));
        when(userNodeProgressRepositoryPort.existsByLearningPathNodeIdAndUserId(node, userId))
                .thenReturn(false);
        when(userLearningProgressRepositoryPort.findByUserId(userId))
                .thenReturn(Optional.of(progress));
        when(chestRepositoryPort.findById(chestId))
                .thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> openChestUseCase.openChest(command)
        );

        assertEquals(ChestErrorCode.CHEST_NOT_FOUND, exception.getErrorCode());
        assertEquals(ChestDetailMessageKey.CHEST_NOT_FOUND, exception.getMessage());
        verify(userRepositoryPort, never()).findById(any());
    }

    @Test
    @DisplayName("UTCID07 - Mở rương báu thất bại khi không tìm thấy thông tin người dùng")
    void UTCID07_UserNotFound() {
        // Arrange
        Long node = 100L;
        Long userId = 1L;
        Long chestId = 10L;

        OpenChestCommand command = new OpenChestCommand(node, userId);

        LearningPathNode chestNode = LearningPathNode.builder()
                .id(node)
                .objectiveId(1L)
                .chestId(chestId)
                .globalOrderIndex(1.0)
                .orderIndex(1.0)
                .nodeType(NodeType.CHEST)
                .build();

        UserLearningProgress progress = mock(UserLearningProgress.class);
        when(progress.getFarthestAvailableNodeGlobalOrderIndex()).thenReturn(5.0);

        Chest chest = mock(Chest.class);

        when(learningPathNodeRepositoryPort.findByIdAndNodeType(node, NodeType.CHEST))
                .thenReturn(Optional.of(chestNode));
        when(userNodeProgressRepositoryPort.existsByLearningPathNodeIdAndUserId(node, userId))
                .thenReturn(false);
        when(userLearningProgressRepositoryPort.findByUserId(userId))
                .thenReturn(Optional.of(progress));
        when(chestRepositoryPort.findById(chestId))
                .thenReturn(Optional.of(chest));
        when(userRepositoryPort.findById(userId))
                .thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> openChestUseCase.openChest(command)
        );

        assertEquals(UserErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_ID_NOT_FOUND, exception.getMessage());
        verify(userNodeProgressRepositoryPort, never()).save(any());
    }
}
