package org.naho.cost.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.cost.port.out.OpenAiCostRepositoryPort;
import org.naho.cost.result.OpenAiCostSummaryResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetOpenAiCostSummaryTest {

    @Mock
    private OpenAiCostRepositoryPort openAiCostRepositoryPort;

    @InjectMocks
    private OpenAiCostUseCase openAiCostUseCase;

    @Test
    @DisplayName("UTCID01 - Lấy tổng quan chi phí OpenAI tháng hiện tại thành công khi có dữ liệu")
    void UTCID01_GetOpenAiCostSummarySuccess() {
        // Arrange
        BigDecimal expectedCost = new BigDecimal("320.00");
        when(openAiCostRepositoryPort.sumCostBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Optional.of(expectedCost));

        // Act
        OpenAiCostSummaryResult result = openAiCostUseCase.getSummary();

        // Assert
        assertNotNull(result);
        assertEquals(expectedCost, result.getCost());
        assertEquals("USD", result.getCurrency());
        assertEquals("MonthToDate", result.getPeriod());
        verify(openAiCostRepositoryPort, times(1)).sumCostBetween(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    @DisplayName("UTCID02 - Lấy tổng quan chi phí OpenAI trả về 0 khi chưa có dữ liệu trong tháng")
    void UTCID02_GetOpenAiCostSummary_NoData_ReturnZero() {
        // Arrange
        when(openAiCostRepositoryPort.sumCostBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Optional.empty());

        // Act
        OpenAiCostSummaryResult result = openAiCostUseCase.getSummary();

        // Assert
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getCost());
        assertEquals("USD", result.getCurrency());
        assertEquals("MonthToDate", result.getPeriod());
        verify(openAiCostRepositoryPort, times(1)).sumCostBetween(any(LocalDate.class), any(LocalDate.class));
    }
}
