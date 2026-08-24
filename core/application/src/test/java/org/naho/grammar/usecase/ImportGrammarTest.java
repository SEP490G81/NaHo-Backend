package org.naho.grammar.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.grammar.exception.GrammarErrorCode;
import org.naho.grammar.port.out.GrammarExcelParserPort;
import org.naho.question.model.Grammar;
import org.naho.question.port.out.SaveGrammarPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImportGrammarTest {

    @Mock
    private GrammarExcelParserPort grammarExcelParserPort;
    @Mock
    private SaveGrammarPort saveGrammarPort;
    @Mock
    private TransactionPort transactionPort;

    @InjectMocks
    private ImportGrammarUseCase importGrammarUseCase;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        lenient().when(transactionPort.execute(any(Supplier.class))).thenAnswer(inv -> {
            Supplier<?> s = inv.getArgument(0);
            return s.get();
        });
    }

    @Test
    @DisplayName("UTCID01 - Import ngữ pháp từ Excel thành công")
    void UTCID01_ImportGrammar_Success() {
        InputStream is = new ByteArrayInputStream(new byte[]{1, 2});
        Grammar grammar = mock(Grammar.class);

        when(grammarExcelParserPort.parseGrammarExcel(is)).thenReturn(List.of(grammar));

        importGrammarUseCase.importGrammar(is);

        verify(saveGrammarPort, times(1)).saveAll(List.of(grammar));
    }

    @Test
    @DisplayName("UTCID02 - Import thất bại do file rỗng")
    void UTCID02_ImportGrammar_Empty() {
        InputStream is = new ByteArrayInputStream(new byte[]{1, 2});
        when(grammarExcelParserPort.parseGrammarExcel(is)).thenReturn(List.of());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> importGrammarUseCase.importGrammar(is));
        assertEquals(GrammarErrorCode.GRAMMAR_IMPORT_EMPTY, ex.getErrorCode());
    }
}
