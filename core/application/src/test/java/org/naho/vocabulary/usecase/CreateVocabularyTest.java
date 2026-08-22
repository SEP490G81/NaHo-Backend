package org.naho.vocabulary.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.question.model.Vocabulary;
import org.naho.vocabulary.command.CreateVocabularyCommand;
import org.naho.vocabulary.port.out.VocabularyRepositoryPort;
import org.naho.vocabulary.result.VocabularyResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateVocabularyTest {

    @Mock
    private VocabularyRepositoryPort vocabularyRepositoryPort;

    @InjectMocks
    private CreateVocabularyUseCase createVocabularyUseCase;

    @Test
    @DisplayName("UTCID01 - Tạo từ vựng thành công")
    void UTCID01_CreateVocabulary_Success() {
        CreateVocabularyCommand command = new CreateVocabularyCommand("あい", "愛", "Tình yêu", "Love");
        Vocabulary vocab = Vocabulary.builder().id(1L).reading("あい").japanese("愛").vietnameseMeaningText("Tình yêu").englishMeaningText("Love").build();

        when(vocabularyRepositoryPort.save(any(Vocabulary.class))).thenReturn(vocab);

        VocabularyResult result = createVocabularyUseCase.createVocabulary(command);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("愛", result.japanese());
        verify(vocabularyRepositoryPort, times(1)).save(any(Vocabulary.class));
    }
}
