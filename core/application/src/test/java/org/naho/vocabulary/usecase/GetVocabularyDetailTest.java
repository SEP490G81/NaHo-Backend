package org.naho.vocabulary.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.question.model.Vocabulary;
import org.naho.shared.exception.ApplicationException;
import org.naho.vocabulary.exception.VocabularyErrorCode;
import org.naho.vocabulary.port.out.VocabularyRepositoryPort;
import org.naho.vocabulary.result.VocabularyResult;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetVocabularyDetailTest {

    @Mock
    private VocabularyRepositoryPort vocabularyRepositoryPort;

    @InjectMocks
    private GetVocabularyDetailUseCase getVocabularyDetailUseCase;

    @Test
    @DisplayName("UTCID01 - Lấy chi tiết từ vựng thành công")
    void UTCID01_GetVocabularyDetail_Success() {
        Vocabulary vocab = Vocabulary.builder().id(1L).japanese("愛").build();
        when(vocabularyRepositoryPort.findById(1L)).thenReturn(Optional.of(vocab));

        VocabularyResult result = getVocabularyDetailUseCase.getVocabularyDetail(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("愛", result.japanese());
    }

    @Test
    @DisplayName("UTCID02 - Lấy chi tiết thất bại do không tìm thấy")
    void UTCID02_GetVocabularyDetail_NotFound() {
        when(vocabularyRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> getVocabularyDetailUseCase.getVocabularyDetail(99L));
        assertEquals(VocabularyErrorCode.VOCABULARY_NOT_FOUND, ex.getErrorCode());
    }
}
