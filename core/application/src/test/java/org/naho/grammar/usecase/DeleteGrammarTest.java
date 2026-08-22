package org.naho.grammar.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.grammar.exception.GrammarErrorCode;
import org.naho.grammar.port.out.GrammarRepositoryPort;
import org.naho.question.model.Grammar;
import org.naho.shared.exception.ApplicationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteGrammarTest {

    @Mock
    private GrammarRepositoryPort grammarRepositoryPort;

    @InjectMocks
    private DeleteGrammarUseCase deleteGrammarUseCase;

    @Test
    @DisplayName("UTCID01 - Xóa ngữ pháp thành công")
    void UTCID01_DeleteGrammar_Success() {
        Grammar grammar = mock(Grammar.class);
        when(grammarRepositoryPort.findById(1L)).thenReturn(Optional.of(grammar));

        deleteGrammarUseCase.deleteGrammar(1L);

        verify(grammarRepositoryPort, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("UTCID02 - Thất bại do không tìm thấy ngữ pháp")
    void UTCID02_DeleteGrammar_NotFound() {
        when(grammarRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> deleteGrammarUseCase.deleteGrammar(99L));
        assertEquals(GrammarErrorCode.GRAMMAR_NOT_FOUND, ex.getErrorCode());
    }
}
