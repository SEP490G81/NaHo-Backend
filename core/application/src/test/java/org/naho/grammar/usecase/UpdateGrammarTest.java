package org.naho.grammar.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.grammar.command.UpdateGrammarCommand;
import org.naho.grammar.exception.GrammarErrorCode;
import org.naho.grammar.port.out.GrammarRepositoryPort;
import org.naho.grammar.result.GrammarResult;
import org.naho.question.model.Grammar;
import org.naho.shared.exception.ApplicationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateGrammarTest {

    @Mock
    private GrammarRepositoryPort grammarRepositoryPort;

    @InjectMocks
    private UpdateGrammarUseCase updateGrammarUseCase;

    @Test
    @DisplayName("UTCID01 - Cập nhật ngữ pháp thành công")
    void UTCID01_UpdateGrammar_Success() {
        UpdateGrammarCommand command = new UpdateGrammarCommand(1L, "です", "です", "Là", "Is/Am/Are");
        Grammar grammar = Grammar.builder().id(1L).reading("です").japanese("です").build();

        when(grammarRepositoryPort.findById(1L)).thenReturn(Optional.of(grammar));
        when(grammarRepositoryPort.save(any(Grammar.class))).thenReturn(grammar);

        GrammarResult result = updateGrammarUseCase.updateGrammar(command);

        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(grammarRepositoryPort, times(1)).save(any(Grammar.class));
    }

    @Test
    @DisplayName("UTCID02 - Thất bại do không tìm thấy ngữ pháp")
    void UTCID02_UpdateGrammar_NotFound() {
        UpdateGrammarCommand command = new UpdateGrammarCommand(99L, "です", "です", "Là", "Is/Am/Are");
        when(grammarRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> updateGrammarUseCase.updateGrammar(command));
        assertEquals(GrammarErrorCode.GRAMMAR_NOT_FOUND, ex.getErrorCode());
    }
}
