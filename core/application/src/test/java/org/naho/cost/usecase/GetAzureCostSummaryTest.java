package org.naho.cost.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.cost.port.out.AzureCostRepositoryPort;
import org.naho.cost.result.AzureCostSummaryResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAzureCostSummaryTest {

    @Mock
    private AzureCostRepositoryPort azureCostRepositoryPort;

    @InjectMocks
    private AzureCostUseCase azureCostUseCase;

    @Test
    @DisplayName("UTCID01 - Lấy tổng quan chi phí Azure tháng hiện tại thành công khi có dữ liệu")
    void UTCID01_GetAzureCostSummarySuccess() {
        // Arrange
        BigDecimal expectedCost = new BigDecimal("245.50");
        when(azureCostRepositoryPort.sumCostBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Optional.of(expectedCost));

        // Act
        AzureCostSummaryResult result = azureCostUseCase.getSummary();

        // Assert
        assertNotNull(result);
        assertEquals(expectedCost, result.getCost());
        assertEquals("USD", result.getCurrency());
        assertEquals("MonthToDate", result.getPeriod());
        verify(azureCostRepositoryPort, times(1)).sumCostBetween(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    @DisplayName("UTCID02 - Lấy tổng quan chi phí Azure trả về 0 khi chưa có dữ liệu trong tháng")
    void UTCID02_GetAzureCostSummary_NoData_ReturnZero() {
        // Arrange
        when(azureCostRepositoryPort.sumCostBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Optional.empty());

        // Act
        AzureCostSummaryResult result = azureCostUseCase.getSummary();

        // Assert
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getCost());
        assertEquals("USD", result.getCurrency());
        assertEquals("MonthToDate", result.getPeriod());
        verify(azureCostRepositoryPort, times(1)).sumCostBetween(any(LocalDate.class), any(LocalDate.class));
    }
}
