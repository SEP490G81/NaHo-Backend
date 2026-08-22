package org.naho.book.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.book.command.UpdateObjectiveCommand;
import org.naho.book.exception.ObjectiveErrorCode;
import org.naho.book.model.Objective;
import org.naho.book.port.out.ObjectiveRepositoryPort;
import org.naho.book.result.ObjectiveListItemResult;
import org.naho.book.type.TopicStatus;
import org.naho.furigana.port.out.FuriganaGenerationPort;
import org.naho.learning.port.out.LearningPathNodeRepositoryPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateObjectiveTest {

    @Mock
    private ObjectiveRepositoryPort objectiveRepositoryPort;
    @Mock
    private LearningPathNodeRepositoryPort learningPathNodeRepositoryPort;
    @Mock
    private TransactionPort transactionPort;
    @Mock
    private FuriganaGenerationPort furiganaGenerationPort;

    @InjectMocks
    private UpdateObjectiveUseCase updateObjectiveUseCase;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        lenient().when(transactionPort.execute(any(Supplier.class))).thenAnswer(inv -> {
            Supplier<?> s = inv.getArgument(0);
            return s.get();
        });
    }

    @Test
    @DisplayName("UTCID01 - Cập nhật Objective thành công")
    void UTCID01_UpdateObjective_Success() {
        UpdateObjectiveCommand command = new UpdateObjectiveCommand(1L, "Obj 1", "Desc 1", TopicStatus.DRAFT, 1L, true);
        Objective obj = Objective.builder().id(1L).japaneseName("Old").orderIndex(1.0).build();

        when(objectiveRepositoryPort.findById(1L)).thenReturn(Optional.of(obj));
        when(furiganaGenerationPort.generateFuriganaMarkup(any())).thenReturn("Markup");
        lenient().when(learningPathNodeRepositoryPort.findByObjectiveId(1L)).thenReturn(List.of());

        ObjectiveListItemResult result = updateObjectiveUseCase.updateObjective(command);

        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(objectiveRepositoryPort, times(1)).save(any(Objective.class));
    }

    @Test
    @DisplayName("UTCID02 - Cập nhật Objective thất bại do không có quyền")
    void UTCID02_UpdateObjective_Forbidden() {
        UpdateObjectiveCommand command = new UpdateObjectiveCommand(1L, "Obj 1", "Desc 1", TopicStatus.DRAFT, 1L, false);

        ApplicationException ex = assertThrows(ApplicationException.class, () -> updateObjectiveUseCase.updateObjective(command));
        assertEquals(ObjectiveErrorCode.OBJECTIVE_UPDATE_FORBIDDEN, ex.getErrorCode());
    }

    @Test
    @DisplayName("UTCID03 - Cập nhật Objective thất bại do không tìm thấy Objective")
    void UTCID03_UpdateObjective_NotFound() {
        UpdateObjectiveCommand command = new UpdateObjectiveCommand(99L, "Obj 1", "Desc 1", TopicStatus.DRAFT, 1L, true);
        when(objectiveRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> updateObjectiveUseCase.updateObjective(command));
        assertEquals(ObjectiveErrorCode.OBJECTIVE_NOT_FOUND, ex.getErrorCode());
    }
}
