package org.naho.book.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.book.command.GetLessonDetailCommand;
import org.naho.book.exception.LessonErrorCode;
import org.naho.book.model.Lesson;
import org.naho.book.model.Objective;
import org.naho.book.port.out.LessonRepositoryPort;
import org.naho.book.port.out.ObjectiveRepositoryPort;
import org.naho.book.result.LessonDetailResult;
import org.naho.shared.exception.ApplicationException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetLessonDetailTest {

    @Mock
    private LessonRepositoryPort lessonRepositoryPort;
    @Mock
    private ObjectiveRepositoryPort objectiveRepositoryPort;

    @InjectMocks
    private GetLessonDetailUseCase getLessonDetailUseCase;

    @Test
    @DisplayName("UTCID01 - Lấy chi tiết Lesson thành công")
    void UTCID01_GetLessonDetail_Success() {
        GetLessonDetailCommand command = new GetLessonDetailCommand(1L);
        Lesson lesson = Lesson.builder().id(1L).japaneseName("Lesson 1").orderIndex(1.0).build();
        Objective obj = Objective.builder().id(10L).japaneseName("Obj 1").orderIndex(1.0).build();

        when(lessonRepositoryPort.findById(1L)).thenReturn(Optional.of(lesson));
        when(objectiveRepositoryPort.findByLessonId(1L)).thenReturn(List.of(obj));

        LessonDetailResult result = getLessonDetailUseCase.getLessonDetail(command);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(1, result.objectives().size());
        verify(lessonRepositoryPort, times(1)).findById(1L);
    }

    @Test
    @DisplayName("UTCID02 - Lấy chi tiết Lesson thất bại khi không tìm thấy Lesson")
    void UTCID02_GetLessonDetail_NotFound() {
        GetLessonDetailCommand command = new GetLessonDetailCommand(99L);
        when(lessonRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> getLessonDetailUseCase.getLessonDetail(command));
        assertEquals(LessonErrorCode.LESSON_NOT_FOUND, ex.getErrorCode());
    }
}
