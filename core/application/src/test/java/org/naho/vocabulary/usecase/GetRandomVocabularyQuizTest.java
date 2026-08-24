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
import org.naho.vocabulary.port.out.RandomVocabularyPort;
import org.naho.vocabulary.result.VocabularyQuizResult;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetRandomVocabularyQuizTest {

    @Mock
    private RandomVocabularyPort randomVocabularyPort;

    @InjectMocks
    private GetRandomVocabularyQuizUseCase getRandomVocabularyQuizUseCase;

    @Test
    @DisplayName("UTCID01 - Lấy ngẫu nhiên câu hỏi quiz từ vựng thành công")
    void UTCID01_GetRandomQuiz_Success() {
        Vocabulary vocab = Vocabulary.builder().id(1L).japanese("愛").reading("あい").vietnameseMeaningText("Tình yêu").build();

        when(randomVocabularyPort.findRandomVocabulary()).thenReturn(Optional.of(vocab));
        when(randomVocabularyPort.findRandomDistractorMeanings(eq(1L), eq("Tình yêu"), anyInt())).thenReturn(List.of("Gia đình", "Bạn bè", "Học tập"));

        List<VocabularyQuizResult> results = getRandomVocabularyQuizUseCase.getRandomQuiz(1);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("愛", results.get(0).japanese());
    }

    @Test
    @DisplayName("UTCID02 - Lấy ngẫu nhiên thất bại do không có từ vựng nào trong hệ thống")
    void UTCID02_GetRandomQuiz_NotFound() {
        when(randomVocabularyPort.findRandomVocabulary()).thenReturn(Optional.empty());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> getRandomVocabularyQuizUseCase.getRandomQuiz(1));
        assertEquals(VocabularyErrorCode.VOCABULARY_NOT_FOUND, ex.getErrorCode());
    }
}
