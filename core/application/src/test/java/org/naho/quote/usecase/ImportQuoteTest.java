package org.naho.quote.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.i18n.message.quote.QuoteDetailMessageKey;
import org.naho.quote.exception.QuoteErrorCode;
import org.naho.quote.model.Quote;
import org.naho.quote.port.out.QuoteExcelParserPort;
import org.naho.quote.port.out.QuotePort;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImportQuoteTest {

    @Mock
    private QuoteExcelParserPort quoteExcelParserPort;

    @Mock
    private QuotePort quotePort;

    @Mock
    private TransactionPort transactionPort;

    @InjectMocks
    private ImportQuoteUseCase importQuoteUseCase;

    @BeforeEach
    void setUp() {
        lenient().doAnswer(invocation -> {
            Supplier<?> supplier = invocation.getArgument(0);
            return supplier.get();
        }).when(transactionPort).execute(any(Supplier.class));
    }

    @Test
    @DisplayName("UTCID01 - Nhập danh sách câu châm ngôn từ file Excel thành công")
    void UTCID01_ImportQuote_Success() {
        // Arrange
        InputStream inputStream = mock(InputStream.class);
        List<Quote> quotes = List.of(
                Quote.builder().id(1L).kanji("七転び八起き").build(),
                Quote.builder().id(2L).kanji("一期一会").build()
        );

        when(quoteExcelParserPort.parseQuoteExcel(inputStream)).thenReturn(quotes);

        // Act
        assertDoesNotThrow(() -> importQuoteUseCase.importQuote(inputStream));

        // Assert
        verify(quoteExcelParserPort, times(1)).parseQuoteExcel(inputStream);
        verify(quotePort, times(1)).saveAll(quotes);
    }

    @Test
    @DisplayName("UTCID02 - Nhập câu châm ngôn thất bại khi danh sách parse được từ Excel rỗng")
    void UTCID02_ImportQuote_EmptyList() {
        // Arrange
        InputStream inputStream = mock(InputStream.class);
        when(quoteExcelParserPort.parseQuoteExcel(inputStream)).thenReturn(Collections.emptyList());

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> importQuoteUseCase.importQuote(inputStream)
        );

        assertEquals(QuoteErrorCode.QUOTE_IMPORT_EMPTY, exception.getErrorCode());
        assertEquals(QuoteDetailMessageKey.QUOTE_IMPORT_EMPTY, exception.getMessage());
        verify(quoteExcelParserPort, times(1)).parseQuoteExcel(inputStream);
        verify(quotePort, never()).saveAll(any());
    }
}
