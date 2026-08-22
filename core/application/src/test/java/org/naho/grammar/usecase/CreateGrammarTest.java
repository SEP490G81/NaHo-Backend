package org.naho.grammar.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.grammar.command.CreateGrammarCommand;
import org.naho.grammar.port.out.GrammarRepositoryPort;
import org.naho.grammar.result.GrammarResult;
import org.naho.question.model.Grammar;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateGrammarTest {

    @Mock
    private GrammarRepositoryPort grammarRepositoryPort;

    @InjectMocks
    private CreateGrammarUseCase createGrammarUseCase;

    @Test
    @DisplayName("UTCID01 - Tạo ngữ pháp thành công")
    void UTCID01_CreateGrammar_Success() {
        CreateGrammarCommand command = new CreateGrammarCommand("です", "です", "Là", "Is/Am/Are");
        Grammar grammar = Grammar.builder().id(1L).reading("です").japanese("です").build();

        when(grammarRepositoryPort.save(any(Grammar.class))).thenReturn(grammar);

        GrammarResult result = createGrammarUseCase.createGrammar(command);

        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(grammarRepositoryPort, times(1)).save(any(Grammar.class));
    }
}
