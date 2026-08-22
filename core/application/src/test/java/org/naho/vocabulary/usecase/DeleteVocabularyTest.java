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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteVocabularyTest {

    @Mock
    private VocabularyRepositoryPort vocabularyRepositoryPort;

    @InjectMocks
    private DeleteVocabularyUseCase deleteVocabularyUseCase;

    @Test
    @DisplayName("UTCID01 - Xóa từ vựng thành công khi tồn tại")
    void UTCID01_DeleteVocabulary_Success() {
        Vocabulary vocab = mock(Vocabulary.class);
        when(vocabularyRepositoryPort.findById(1L)).thenReturn(Optional.of(vocab));

        deleteVocabularyUseCase.deleteVocabulary(1L);

        verify(vocabularyRepositoryPort, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("UTCID02 - Thất bại do không tìm thấy từ vựng")
    void UTCID02_DeleteVocabulary_NotFound() {
        when(vocabularyRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> deleteVocabularyUseCase.deleteVocabulary(99L));
        assertEquals(VocabularyErrorCode.VOCABULARY_NOT_FOUND, ex.getErrorCode());
    }
}
