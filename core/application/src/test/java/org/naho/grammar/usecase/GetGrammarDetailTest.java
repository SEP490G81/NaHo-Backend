package org.naho.grammar.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.grammar.exception.GrammarErrorCode;
import org.naho.grammar.port.out.GrammarRepositoryPort;
import org.naho.grammar.result.GrammarResult;
import org.naho.question.model.Grammar;
import org.naho.shared.exception.ApplicationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetGrammarDetailTest {

    @Mock
    private GrammarRepositoryPort grammarRepositoryPort;

    @InjectMocks
    private GetGrammarDetailUseCase getGrammarDetailUseCase;

    @Test
    @DisplayName("UTCID01 - Lấy chi tiết ngữ pháp thành công")
    void UTCID01_GetGrammarDetail_Success() {
        Grammar grammar = Grammar.builder().id(1L).japanese("です").build();
        when(grammarRepositoryPort.findById(1L)).thenReturn(Optional.of(grammar));

        GrammarResult result = getGrammarDetailUseCase.getGrammarDetail(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("です", result.japanese());
    }

    @Test
    @DisplayName("UTCID02 - Thất bại do không tìm thấy")
    void UTCID02_GetGrammarDetail_NotFound() {
        when(grammarRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> getGrammarDetailUseCase.getGrammarDetail(99L));
        assertEquals(GrammarErrorCode.GRAMMAR_NOT_FOUND, ex.getErrorCode());
    }
}
