package org.naho.quote.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.i18n.message.quote.QuoteDetailMessageKey;
import org.naho.quote.exception.QuoteErrorCode;
import org.naho.quote.model.Quote;
import org.naho.quote.port.out.QuotePort;
import org.naho.quote.result.QuoteResult;
import org.naho.shared.exception.ApplicationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetRandomQuoteTest {

    @Mock
    private QuotePort quotePort;

    @InjectMocks
    private GetRandomQuoteUseCase getRandomQuoteUseCase;

    @Test
    @DisplayName("UTCID01 - Lấy câu châm ngôn ngẫu nhiên thành công khi có dữ liệu trong database")
    void UTCID01_GetRandomQuote_Found_Success() {
        // Arrange
        Quote quote = Quote.builder()
                .id(1L)
                .kanji("七転び八起き")
                .hiragana("ななころびやおき")
                .romaji("nanakorobi yaoki")
                .translation("Ngã bảy lần, đứng dậy tám lần")
                .kanjiDetail("七: Thất, 転: Chuyển, 八: Bát, 起: Khởi")
                .build();

        when(quotePort.findRandomQuote()).thenReturn(Optional.of(quote));

        // Act
        QuoteResult result = getRandomQuoteUseCase.getRandomQuote();

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("七転び八起き", result.kanji());
        assertEquals("ななころびやおき", result.hiragana());
        assertEquals("nanakorobi yaoki", result.romaji());
        assertEquals("Ngã bảy lần, đứng dậy tám lần", result.translation());
        assertEquals("七: Thất, 転: Chuyển, 八: Bát, 起: Khởi", result.kanjiDetail());

        verify(quotePort, times(1)).findRandomQuote();
    }

    @Test
    @DisplayName("UTCID02 - Lấy câu châm ngôn ngẫu nhiên thất bại khi không tìm thấy câu nào")
    void UTCID02_GetRandomQuote_NotFound() {
        // Arrange
        when(quotePort.findRandomQuote()).thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> getRandomQuoteUseCase.getRandomQuote()
        );

        assertEquals(QuoteErrorCode.QUOTE_NOT_FOUND, exception.getErrorCode());
        assertEquals(QuoteDetailMessageKey.QUOTE_NOT_FOUND, exception.getMessage());
        verify(quotePort, times(1)).findRandomQuote();
    }
}
