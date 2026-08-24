package org.naho.vocabulary.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.question.model.Vocabulary;
import org.naho.shared.exception.ApplicationException;
import org.naho.vocabulary.command.UpdateVocabularyCommand;
import org.naho.vocabulary.exception.VocabularyErrorCode;
import org.naho.vocabulary.port.out.VocabularyRepositoryPort;
import org.naho.vocabulary.result.VocabularyResult;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateVocabularyTest {

    @Mock
    private VocabularyRepositoryPort vocabularyRepositoryPort;

    @InjectMocks
    private UpdateVocabularyUseCase updateVocabularyUseCase;

    @Test
    @DisplayName("UTCID01 - Cập nhật từ vựng thành công")
    void UTCID01_UpdateVocabulary_Success() {
        UpdateVocabularyCommand command = new UpdateVocabularyCommand(1L, "あい", "愛", "Tình yêu", "Love");
        Vocabulary vocab = Vocabulary.builder().id(1L).reading("あい").japanese("愛").build();

        when(vocabularyRepositoryPort.findById(1L)).thenReturn(Optional.of(vocab));
        when(vocabularyRepositoryPort.save(any(Vocabulary.class))).thenReturn(vocab);

        VocabularyResult result = updateVocabularyUseCase.updateVocabulary(command);

        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(vocabularyRepositoryPort, times(1)).save(any(Vocabulary.class));
    }

    @Test
    @DisplayName("UTCID02 - Cập nhật thất bại do không tìm thấy từ vựng")
    void UTCID02_UpdateVocabulary_NotFound() {
        UpdateVocabularyCommand command = new UpdateVocabularyCommand(99L, "あい", "愛", "Tình yêu", "Love");
        when(vocabularyRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> updateVocabularyUseCase.updateVocabulary(command));
        assertEquals(VocabularyErrorCode.VOCABULARY_NOT_FOUND, ex.getErrorCode());
    }
}
