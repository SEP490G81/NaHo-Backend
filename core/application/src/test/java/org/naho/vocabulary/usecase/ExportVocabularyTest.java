package org.naho.vocabulary.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.question.model.Vocabulary;
import org.naho.question.port.out.VocabulariesQuestionPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.vocabulary.exception.VocabularyErrorCode;
import org.naho.vocabulary.port.out.ExcelWriterPort;
import org.naho.vocabulary.port.out.VocabularyRepositoryPort;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExportVocabularyTest {

    @Mock
    private VocabularyRepositoryPort vocabularyRepositoryPort;
    @Mock
    private VocabulariesQuestionPort vocabulariesQuestionPort;
    @Mock
    private ExcelWriterPort excelWriterPort;

    @InjectMocks
    private ExportVocabularyUseCase exportVocabularyUseCase;

    @Test
    @DisplayName("UTCID01 - Export từ vựng theo questionId thành công")
    void UTCID01_ExportByQuestion_Success() {
        Vocabulary v = mock(Vocabulary.class);
        ByteArrayInputStream is = new ByteArrayInputStream(new byte[]{1});

        when(vocabularyRepositoryPort.findVocabularyList(10L)).thenReturn(List.of(v));
        when(excelWriterPort.writeVocabulariesToExcel(List.of(v))).thenReturn(is);

        ByteArrayInputStream result = exportVocabularyUseCase.exportByQuestion(10L);

        assertNotNull(result);
        verify(excelWriterPort, times(1)).writeVocabulariesToExcel(anyList());
    }

    @Test
    @DisplayName("UTCID02 - Export thất bại do danh sách từ vựng rỗng")
    void UTCID02_ExportByQuestion_NotFound() {
        when(vocabularyRepositoryPort.findVocabularyList(99L)).thenReturn(List.of());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> exportVocabularyUseCase.exportByQuestion(99L));
        assertEquals(VocabularyErrorCode.VOCABULARY_EXPORT_NOT_FOUND, ex.getErrorCode());
    }
}
