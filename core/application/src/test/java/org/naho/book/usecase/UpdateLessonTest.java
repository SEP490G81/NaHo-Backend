package org.naho.book.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.book.command.UpdateLessonCommand;
import org.naho.book.exception.LessonErrorCode;
import org.naho.book.model.Lesson;
import org.naho.book.port.out.LessonRepositoryPort;
import org.naho.book.result.LessonListItemResult;
import org.naho.book.type.TopicStatus;
import org.naho.furigana.port.out.FuriganaGenerationPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;

import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateLessonTest {

    @Mock
    private LessonRepositoryPort lessonRepositoryPort;
    @Mock
    private TransactionPort transactionPort;
    @Mock
    private FuriganaGenerationPort furiganaGenerationPort;

    @InjectMocks
    private UpdateLessonUseCase updateLessonUseCase;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        lenient().when(transactionPort.execute(any(Supplier.class))).thenAnswer(inv -> {
            Supplier<?> s = inv.getArgument(0);
            return s.get();
        });
    }

    @Test
    @DisplayName("UTCID01 - Cập nhật Lesson thành công")
    void UTCID01_UpdateLesson_Success() {
        UpdateLessonCommand command = new UpdateLessonCommand(1L, "Lesson 1", "Desc 1", TopicStatus.DRAFT, 1L, true);
        Lesson lesson = Lesson.builder().id(1L).japaneseName("Old").orderIndex(1.0).build();

        when(lessonRepositoryPort.findById(1L)).thenReturn(Optional.of(lesson));
        when(furiganaGenerationPort.generateFuriganaMarkup(any())).thenReturn("Markup");

        LessonListItemResult result = updateLessonUseCase.updateLesson(command);

        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(lessonRepositoryPort, times(1)).save(any(Lesson.class));
    }

    @Test
    @DisplayName("UTCID02 - Cập nhật Lesson thất bại do không có quyền")
    void UTCID02_UpdateLesson_Forbidden() {
        UpdateLessonCommand command = new UpdateLessonCommand(1L, "Lesson 1", "Desc 1", TopicStatus.DRAFT, 1L, false);

        ApplicationException ex = assertThrows(ApplicationException.class, () -> updateLessonUseCase.updateLesson(command));
        assertEquals(LessonErrorCode.LESSON_UPDATE_FORBIDDEN, ex.getErrorCode());
    }

    @Test
    @DisplayName("UTCID03 - Cập nhật Lesson thất bại do không tìm thấy Lesson")
    void UTCID03_UpdateLesson_NotFound() {
        UpdateLessonCommand command = new UpdateLessonCommand(99L, "Lesson 1", "Desc 1", TopicStatus.DRAFT, 1L, true);
        when(lessonRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> updateLessonUseCase.updateLesson(command));
        assertEquals(LessonErrorCode.LESSON_NOT_FOUND, ex.getErrorCode());
    }
}
