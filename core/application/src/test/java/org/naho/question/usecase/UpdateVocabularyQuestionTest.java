package org.naho.question.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.question.command.UpdateVocabularyQuestionCommand;
import org.naho.question.exception.VocabularyQuestionErrorCode;
import org.naho.question.model.VocabularyQuestion;
import org.naho.question.port.out.VocabularyQuestionRepositoryPort;
import org.naho.question.result.UpdateVocabularyQuestionResult;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.vocabulary.port.out.VocabularyRepositoryPort;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateVocabularyQuestionTest {

    @Mock
    private VocabularyQuestionRepositoryPort vocabularyQuestionRepositoryPort;
    @Mock
    private VocabularyRepositoryPort vocabularyRepositoryPort;
    @Mock
    private TransactionPort transactionPort;

    @InjectMocks
    private UpdateVocabularyQuestionUseCase updateVocabularyQuestionUseCase;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        lenient().when(transactionPort.execute(any(Supplier.class))).thenAnswer(inv -> {
            Supplier<?> s = inv.getArgument(0);
            return s.get();
        });
    }

    @Test
    @DisplayName("UTCID01 - Cập nhật VocabularyQuestion thành công")
    void UTCID01_UpdateVocabularyQuestion_Success() {
        UpdateVocabularyQuestionCommand command = new UpdateVocabularyQuestionCommand(1L, List.of());
        VocabularyQuestion question = VocabularyQuestion.builder().id(1L).vocabularies(List.of()).build();

        when(vocabularyQuestionRepositoryPort.findById(1L)).thenReturn(Optional.of(question));
        when(vocabularyQuestionRepositoryPort.save(any(VocabularyQuestion.class))).thenReturn(question);

        UpdateVocabularyQuestionResult result = updateVocabularyQuestionUseCase.updateVocabularyQuestion(command);

        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(vocabularyQuestionRepositoryPort, times(1)).save(any(VocabularyQuestion.class));
    }

    @Test
    @DisplayName("UTCID02 - Thất bại do không tìm thấy VocabularyQuestion")
    void UTCID02_UpdateVocabularyQuestion_NotFound() {
        UpdateVocabularyQuestionCommand command = new UpdateVocabularyQuestionCommand(99L, List.of());
        when(vocabularyQuestionRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> updateVocabularyQuestionUseCase.updateVocabularyQuestion(command));
        assertEquals(VocabularyQuestionErrorCode.VOCABULARY_QUESTION_NOT_FOUND, ex.getErrorCode());
    }
}
