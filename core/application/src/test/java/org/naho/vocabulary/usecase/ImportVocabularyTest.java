package org.naho.vocabulary.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.question.model.Vocabulary;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.vocabulary.exception.VocabularyErrorCode;
import org.naho.vocabulary.port.out.SaveVocabularyPort;
import org.naho.vocabulary.port.out.VocabularyExcelParserPort;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImportVocabularyTest {

    @Mock
    private VocabularyExcelParserPort vocabularyExcelParserPort;
    @Mock
    private SaveVocabularyPort saveVocabularyPort;
    @Mock
    private TransactionPort transactionPort;

    @InjectMocks
    private ImportVocabularyUseCase importVocabularyUseCase;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        lenient().when(transactionPort.execute(any(Supplier.class))).thenAnswer(inv -> {
            Supplier<?> s = inv.getArgument(0);
            return s.get();
        });
    }

    @Test
    @DisplayName("UTCID01 - Import từ vựng từ Excel thành công")
    void UTCID01_ImportVocabulary_Success() {
        InputStream is = new ByteArrayInputStream(new byte[]{1, 2});
        Vocabulary vocab = mock(Vocabulary.class);

        when(vocabularyExcelParserPort.parseVocabularyExcel(is)).thenReturn(List.of(vocab));

        importVocabularyUseCase.importVocabulary(is);

        verify(saveVocabularyPort, times(1)).saveAll(List.of(vocab));
    }

    @Test
    @DisplayName("UTCID02 - Import thất bại do danh sách từ vựng rỗng")
    void UTCID02_ImportVocabulary_Empty() {
        InputStream is = new ByteArrayInputStream(new byte[]{1, 2});
        when(vocabularyExcelParserPort.parseVocabularyExcel(is)).thenReturn(List.of());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> importVocabularyUseCase.importVocabulary(is));
        assertEquals(VocabularyErrorCode.VOCABULARY_IMPORT_EMPTY, ex.getErrorCode());
    }
}
