package org.naho.book.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.book.command.GetObjectiveDetailCommand;
import org.naho.book.exception.ObjectiveErrorCode;
import org.naho.book.model.Objective;
import org.naho.book.port.out.ObjectiveRepositoryPort;
import org.naho.book.result.ObjectiveDetailResult;
import org.naho.learning.model.LearningPathNode;
import org.naho.learning.port.out.LearningPathNodeRepositoryPort;
import org.naho.shared.exception.ApplicationException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetObjectiveDetailTest {

    @Mock
    private ObjectiveRepositoryPort objectiveRepositoryPort;
    @Mock
    private LearningPathNodeRepositoryPort learningPathNodeRepositoryPort;

    @InjectMocks
    private GetObjectiveDetailUseCase getObjectiveDetailUseCase;

    @Test
    @DisplayName("UTCID01 - Lấy chi tiết Objective thành công")
    void UTCID01_GetObjectiveDetail_Success() {
        GetObjectiveDetailCommand command = new GetObjectiveDetailCommand(1L);
        Objective obj = Objective.builder().id(1L).japaneseName("Obj 1").orderIndex(1.0).build();
        LearningPathNode node = mock(LearningPathNode.class);

        when(node.getId()).thenReturn(10L);
        when(node.getObjectiveId()).thenReturn(1L);
        when(objectiveRepositoryPort.findById(1L)).thenReturn(Optional.of(obj));
        when(learningPathNodeRepositoryPort.findByObjectiveId(1L)).thenReturn(List.of(node));

        ObjectiveDetailResult result = getObjectiveDetailUseCase.getObjectiveDetail(command);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(1, result.learningPathNodes().size());
    }

    @Test
    @DisplayName("UTCID02 - Lấy chi tiết Objective thất bại khi không tìm thấy Objective")
    void UTCID02_GetObjectiveDetail_NotFound() {
        GetObjectiveDetailCommand command = new GetObjectiveDetailCommand(99L);
        when(objectiveRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> getObjectiveDetailUseCase.getObjectiveDetail(command));
        assertEquals(ObjectiveErrorCode.OBJECTIVE_NOT_FOUND, ex.getErrorCode());
    }
}
