package org.naho.cost.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.cost.port.out.AwsCostRepositoryPort;
import org.naho.cost.result.AwsCostSummaryResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAwsCostSummaryTest {

    @Mock
    private AwsCostRepositoryPort awsCostRepositoryPort;

    @InjectMocks
    private AwsCostUseCase awsCostUseCase;

    @Test
    @DisplayName("UTCID01 - Lấy tổng quan chi phí AWS tháng hiện tại thành công khi có dữ liệu")
    void UTCID01_GetAwsCostSummarySuccess() {
        // Arrange
        BigDecimal expectedCost = new BigDecimal("150.75");
        when(awsCostRepositoryPort.sumCostBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Optional.of(expectedCost));

        // Act
        AwsCostSummaryResult result = awsCostUseCase.getSummary();

        // Assert
        assertNotNull(result);
        assertEquals(expectedCost, result.getCost());
        assertEquals("USD", result.getCurrency());
        assertEquals("MonthToDate", result.getPeriod());
        verify(awsCostRepositoryPort, times(1)).sumCostBetween(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    @DisplayName("UTCID02 - Lấy tổng quan chi phí AWS trả về 0 khi chưa có dữ liệu trong tháng")
    void UTCID02_GetAwsCostSummary_NoData_ReturnZero() {
        // Arrange
        when(awsCostRepositoryPort.sumCostBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Optional.empty());

        // Act
        AwsCostSummaryResult result = awsCostUseCase.getSummary();

        // Assert
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getCost());
        assertEquals("USD", result.getCurrency());
        assertEquals("MonthToDate", result.getPeriod());
        verify(awsCostRepositoryPort, times(1)).sumCostBetween(any(LocalDate.class), any(LocalDate.class));
    }
}
