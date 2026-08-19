package org.naho.cost.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.cost.command.AwsCostQueryCommand;
import org.naho.cost.model.AwsDailyCost;
import org.naho.cost.port.out.AwsCostRepositoryPort;
import org.naho.cost.result.AwsCostChartResult;
import org.naho.cost.result.AwsCostPointResult;
import org.naho.shared.constant.SystemZoneId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAwsCostChartDataTest {

    @Mock
    private AwsCostRepositoryPort awsCostRepositoryPort;

    @InjectMocks
    private AwsCostUseCase awsCostUseCase;

    @Test
    @DisplayName("UTCID01 - Lấy biểu đồ chi phí AWS theo ngày với khoảng thời gian tuỳ chỉnh thành công")
    void UTCID01_GetChartData_Daily_WithCustomDateRange_Success() {
        // Arrange
        ZonedDateTime fromZdt = ZonedDateTime.of(2026, 8, 1, 0, 0, 0, 0, SystemZoneId.HO_CHI_MINH_ZONE_ID);
        ZonedDateTime toZdt = ZonedDateTime.of(2026, 8, 10, 0, 0, 0, 0, SystemZoneId.HO_CHI_MINH_ZONE_ID);
        AwsCostQueryCommand command = new AwsCostQueryCommand("Custom", "Daily", fromZdt, toZdt);

        AwsDailyCost daily1 = AwsDailyCost.builder()
                .id(1L)
                .recordDate(LocalDate.of(2026, 8, 1))
                .costAmount(new BigDecimal("12.50"))
                .currency("USD")
                .build();
        AwsDailyCost daily2 = AwsDailyCost.builder()
                .id(2L)
                .recordDate(LocalDate.of(2026, 8, 2))
                .costAmount(new BigDecimal("17.50"))
                .currency("USD")
                .build();

        when(awsCostRepositoryPort.findDailyCostsBetween(fromZdt.toLocalDate(), toZdt.toLocalDate()))
                .thenReturn(List.of(daily1, daily2));

        // Act
        AwsCostChartResult result = awsCostUseCase.getChartData(command);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("30.00"), result.getTotalCost());
        assertEquals("USD", result.getCurrency());
        assertEquals("Daily", result.getGranularity());
        assertEquals(2, result.getPoints().size());
        assertEquals("20260801", result.getPoints().get(0).getDateOrMonth());
        assertEquals(new BigDecimal("12.50"), result.getPoints().get(0).getCost());
        assertEquals("20260802", result.getPoints().get(1).getDateOrMonth());
        assertEquals(new BigDecimal("17.50"), result.getPoints().get(1).getCost());
        verify(awsCostRepositoryPort, times(1)).findDailyCostsBetween(fromZdt.toLocalDate(), toZdt.toLocalDate());
    }

    @Test
    @DisplayName("UTCID02 - Lấy biểu đồ chi phí AWS theo ngày mặc định 30 ngày gần nhất khi fromDate/toDate là null")
    void UTCID02_GetChartData_Daily_DefaultDateRange_WhenDatesNull() {
        // Arrange
        AwsCostQueryCommand command = new AwsCostQueryCommand(null, "Daily", null, null);

        when(awsCostRepositoryPort.findDailyCostsBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of());

        // Act
        AwsCostChartResult result = awsCostUseCase.getChartData(command);

        // Assert
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getTotalCost());
        assertEquals("USD", result.getCurrency());
        assertEquals("Daily", result.getGranularity());
        assertTrue(result.getPoints().isEmpty());
        verify(awsCostRepositoryPort, times(1)).findDailyCostsBetween(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    @DisplayName("UTCID03 - Lấy biểu đồ chi phí AWS theo ngày xử lý an toàn khi costAmount, recordDate, currency bị null")
    void UTCID03_GetChartData_Daily_NullCostAmountAndDateAndCurrency_Handled() {
        // Arrange
        AwsCostQueryCommand command = new AwsCostQueryCommand(null, "Daily", null, null);
        AwsDailyCost dailyWithNulls = AwsDailyCost.builder()
                .id(1L)
                .recordDate(null)
                .costAmount(null)
                .currency(null)
                .build();

        when(awsCostRepositoryPort.findDailyCostsBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(dailyWithNulls));

        // Act
        AwsCostChartResult result = awsCostUseCase.getChartData(command);

        // Assert
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getTotalCost());
        assertEquals(1, result.getPoints().size());
        assertEquals("", result.getPoints().get(0).getDateOrMonth());
        assertEquals(BigDecimal.ZERO, result.getPoints().get(0).getCost());
        assertEquals("USD", result.getPoints().get(0).getCurrency());
    }

    @Test
    @DisplayName("UTCID04 - Lấy biểu đồ chi phí AWS theo tháng với khung thời gian Last3Months")
    void UTCID04_GetChartData_Monthly_Last3Months() {
        // Arrange
        AwsCostQueryCommand command = new AwsCostQueryCommand("Last3Months", "Monthly", null, null);
        List<AwsCostPointResult> mockPoints = List.of(
                new AwsCostPointResult("2026-06", new BigDecimal("100.00"), "USD"),
                new AwsCostPointResult("2026-07", new BigDecimal("120.00"), "USD"),
                new AwsCostPointResult("2026-08", new BigDecimal("80.00"), "USD")
        );

        when(awsCostRepositoryPort.findMonthlyCostsSummary(any(LocalDate.class)))
                .thenReturn(mockPoints);

        // Act
        AwsCostChartResult result = awsCostUseCase.getChartData(command);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("300.00"), result.getTotalCost());
        assertEquals("USD", result.getCurrency());
        assertEquals("Monthly", result.getGranularity());
        assertEquals(3, result.getPoints().size());
        verify(awsCostRepositoryPort, times(1)).findMonthlyCostsSummary(any(LocalDate.class));
    }

    @Test
    @DisplayName("UTCID05 - Lấy biểu đồ chi phí AWS theo tháng với khung thời gian Last12Months")
    void UTCID05_GetChartData_Monthly_Last12Months() {
        // Arrange
        AwsCostQueryCommand command = new AwsCostQueryCommand("Last12Months", "Monthly", null, null);
        List<AwsCostPointResult> mockPoints = List.of(
                new AwsCostPointResult("2025-09", new BigDecimal("50.00"), "USD"),
                new AwsCostPointResult("2026-08", new BigDecimal("70.00"), "USD")
        );

        when(awsCostRepositoryPort.findMonthlyCostsSummary(any(LocalDate.class)))
                .thenReturn(mockPoints);

        // Act
        AwsCostChartResult result = awsCostUseCase.getChartData(command);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("120.00"), result.getTotalCost());
        assertEquals("Monthly", result.getGranularity());
        assertEquals(2, result.getPoints().size());
        verify(awsCostRepositoryPort, times(1)).findMonthlyCostsSummary(any(LocalDate.class));
    }

    @Test
    @DisplayName("UTCID06 - Lấy biểu đồ chi phí AWS theo tháng với khung thời gian Custom và có fromDate")
    void UTCID06_GetChartData_Monthly_CustomWithFromDate() {
        // Arrange
        ZonedDateTime fromZdt = ZonedDateTime.of(2026, 1, 15, 0, 0, 0, 0, SystemZoneId.HO_CHI_MINH_ZONE_ID);
        AwsCostQueryCommand command = new AwsCostQueryCommand("Custom", "Monthly", fromZdt, null);
        List<AwsCostPointResult> mockPoints = List.of(
                new AwsCostPointResult("2026-01", new BigDecimal("90.00"), "USD")
        );

        when(awsCostRepositoryPort.findMonthlyCostsSummary(any(LocalDate.class)))
                .thenReturn(mockPoints);

        // Act
        AwsCostChartResult result = awsCostUseCase.getChartData(command);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("90.00"), result.getTotalCost());
        assertEquals("Monthly", result.getGranularity());
        assertEquals(1, result.getPoints().size());
        verify(awsCostRepositoryPort, times(1)).findMonthlyCostsSummary(any(LocalDate.class));
    }

    @Test
    @DisplayName("UTCID07 - Lấy biểu đồ chi phí AWS theo tháng với khung thời gian mặc định Last6Months")
    void UTCID07_GetChartData_Monthly_Last6Months_Default() {
        // Arrange
        AwsCostQueryCommand command = new AwsCostQueryCommand("Last6Months", "Monthly", null, null);
        List<AwsCostPointResult> mockPoints = List.of(
                new AwsCostPointResult("2026-03", new BigDecimal("60.00"), "USD"),
                new AwsCostPointResult("2026-04", null, "USD"),
                new AwsCostPointResult("2026-05", new BigDecimal("80.00"), "USD")
        );

        when(awsCostRepositoryPort.findMonthlyCostsSummary(any(LocalDate.class)))
                .thenReturn(mockPoints);

        // Act
        AwsCostChartResult result = awsCostUseCase.getChartData(command);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("140.00"), result.getTotalCost());
        assertEquals("Monthly", result.getGranularity());
        assertEquals(3, result.getPoints().size());
        verify(awsCostRepositoryPort, times(1)).findMonthlyCostsSummary(any(LocalDate.class));
    }

    @Test
    @DisplayName("UTCID08 - Lấy biểu đồ chi phí AWS khi command null mặc định Last6Months và Monthly")
    void UTCID08_GetChartData_NullCommand_DefaultMonthly() {
        // Arrange
        when(awsCostRepositoryPort.findMonthlyCostsSummary(any(LocalDate.class)))
                .thenReturn(List.of());

        // Act
        AwsCostChartResult result = awsCostUseCase.getChartData(null);

        // Assert
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getTotalCost());
        assertEquals("USD", result.getCurrency());
        assertEquals("Monthly", result.getGranularity());
        assertTrue(result.getPoints().isEmpty());
        verify(awsCostRepositoryPort, times(1)).findMonthlyCostsSummary(any(LocalDate.class));
    }
}
