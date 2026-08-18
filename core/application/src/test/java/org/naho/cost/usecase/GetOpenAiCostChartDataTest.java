package org.naho.cost.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.cost.model.OpenAiDailyCost;
import org.naho.cost.port.out.OpenAiCostRepositoryPort;
import org.naho.cost.result.OpenAiCostChartResult;
import org.naho.cost.result.OpenAiCostPointResult;
import org.naho.shared.constant.SystemZoneId;
import org.naho.speech.llm.conversation.command.OpenAiCostQueryCommand;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetOpenAiCostChartDataTest {

    @Mock
    private OpenAiCostRepositoryPort openAiCostRepositoryPort;

    @InjectMocks
    private OpenAiCostUseCase openAiCostUseCase;

    @Test
    @DisplayName("UTCID01 - Lấy biểu đồ chi phí OpenAI theo ngày với khoảng thời gian tuỳ chỉnh thành công")
    void UTCID01_GetChartData_Daily_WithCustomDateRange_Success() {
        // Arrange
        ZonedDateTime fromZdt = ZonedDateTime.of(2026, 8, 1, 0, 0, 0, 0, SystemZoneId.HO_CHI_MINH_ZONE_ID);
        ZonedDateTime toZdt = ZonedDateTime.of(2026, 8, 10, 0, 0, 0, 0, SystemZoneId.HO_CHI_MINH_ZONE_ID);
        OpenAiCostQueryCommand command = new OpenAiCostQueryCommand("Custom", "Daily", fromZdt, toZdt);

        OpenAiDailyCost daily1 = OpenAiDailyCost.builder()
                .id(1L)
                .recordDate(LocalDate.of(2026, 8, 1))
                .costAmount(new BigDecimal("40.00"))
                .currency("USD")
                .build();
        OpenAiDailyCost daily2 = OpenAiDailyCost.builder()
                .id(2L)
                .recordDate(LocalDate.of(2026, 8, 2))
                .costAmount(new BigDecimal("50.00"))
                .currency("USD")
                .build();

        when(openAiCostRepositoryPort.findDailyCostsBetween(fromZdt.toLocalDate(), toZdt.toLocalDate()))
                .thenReturn(List.of(daily1, daily2));

        // Act
        OpenAiCostChartResult result = openAiCostUseCase.getChartData(command);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("90.00"), result.getTotalCost());
        assertEquals("USD", result.getCurrency());
        assertEquals("Daily", result.getGranularity());
        assertEquals(2, result.getPoints().size());
        assertEquals("20260801", result.getPoints().get(0).getDateOrMonth());
        assertEquals(new BigDecimal("40.00"), result.getPoints().get(0).getCost());
        assertEquals("20260802", result.getPoints().get(1).getDateOrMonth());
        assertEquals(new BigDecimal("50.00"), result.getPoints().get(1).getCost());
        verify(openAiCostRepositoryPort, times(1)).findDailyCostsBetween(fromZdt.toLocalDate(), toZdt.toLocalDate());
    }

    @Test
    @DisplayName("UTCID02 - Lấy biểu đồ chi phí OpenAI theo ngày mặc định 30 ngày gần nhất khi fromDate/toDate là null")
    void UTCID02_GetChartData_Daily_DefaultDateRange_WhenDatesNull() {
        // Arrange
        OpenAiCostQueryCommand command = new OpenAiCostQueryCommand(null, "Daily", null, null);

        when(openAiCostRepositoryPort.findDailyCostsBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of());

        // Act
        OpenAiCostChartResult result = openAiCostUseCase.getChartData(command);

        // Assert
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getTotalCost());
        assertEquals("USD", result.getCurrency());
        assertEquals("Daily", result.getGranularity());
        assertTrue(result.getPoints().isEmpty());
        verify(openAiCostRepositoryPort, times(1)).findDailyCostsBetween(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    @DisplayName("UTCID03 - Lấy biểu đồ chi phí OpenAI theo ngày xử lý an toàn khi costAmount, recordDate, currency bị null")
    void UTCID03_GetChartData_Daily_NullCostAmountAndDateAndCurrency_Handled() {
        // Arrange
        OpenAiCostQueryCommand command = new OpenAiCostQueryCommand(null, "Daily", null, null);
        OpenAiDailyCost dailyWithNulls = OpenAiDailyCost.builder()
                .id(1L)
                .recordDate(null)
                .costAmount(null)
                .currency(null)
                .build();

        when(openAiCostRepositoryPort.findDailyCostsBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(dailyWithNulls));

        // Act
        OpenAiCostChartResult result = openAiCostUseCase.getChartData(command);

        // Assert
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getTotalCost());
        assertEquals(1, result.getPoints().size());
        assertEquals("", result.getPoints().get(0).getDateOrMonth());
        assertEquals(BigDecimal.ZERO, result.getPoints().get(0).getCost());
        assertEquals("USD", result.getPoints().get(0).getCurrency());
    }

    @Test
    @DisplayName("UTCID04 - Lấy biểu đồ chi phí OpenAI theo tháng với khung thời gian Last3Months")
    void UTCID04_GetChartData_Monthly_Last3Months() {
        // Arrange
        OpenAiCostQueryCommand command = new OpenAiCostQueryCommand("Last3Months", "Monthly", null, null);
        List<OpenAiCostPointResult> mockPoints = List.of(
                new OpenAiCostPointResult("2026-06", new BigDecimal("200.00"), "USD"),
                new OpenAiCostPointResult("2026-07", new BigDecimal("250.00"), "USD"),
                new OpenAiCostPointResult("2026-08", new BigDecimal("150.00"), "USD")
        );

        when(openAiCostRepositoryPort.findMonthlyCostsSummary(any(LocalDate.class)))
                .thenReturn(mockPoints);

        // Act
        OpenAiCostChartResult result = openAiCostUseCase.getChartData(command);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("600.00"), result.getTotalCost());
        assertEquals("USD", result.getCurrency());
        assertEquals("Monthly", result.getGranularity());
        assertEquals(3, result.getPoints().size());
        verify(openAiCostRepositoryPort, times(1)).findMonthlyCostsSummary(any(LocalDate.class));
    }

    @Test
    @DisplayName("UTCID05 - Lấy biểu đồ chi phí OpenAI theo tháng với khung thời gian Last12Months")
    void UTCID05_GetChartData_Monthly_Last12Months() {
        // Arrange
        OpenAiCostQueryCommand command = new OpenAiCostQueryCommand("Last12Months", "Monthly", null, null);
        List<OpenAiCostPointResult> mockPoints = List.of(
                new OpenAiCostPointResult("2025-09", new BigDecimal("120.00"), "USD"),
                new OpenAiCostPointResult("2026-08", new BigDecimal("180.00"), "USD")
        );

        when(openAiCostRepositoryPort.findMonthlyCostsSummary(any(LocalDate.class)))
                .thenReturn(mockPoints);

        // Act
        OpenAiCostChartResult result = openAiCostUseCase.getChartData(command);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("300.00"), result.getTotalCost());
        assertEquals("Monthly", result.getGranularity());
        assertEquals(2, result.getPoints().size());
        verify(openAiCostRepositoryPort, times(1)).findMonthlyCostsSummary(any(LocalDate.class));
    }

    @Test
    @DisplayName("UTCID06 - Lấy biểu đồ chi phí OpenAI theo tháng với khung thời gian Custom và có fromDate")
    void UTCID06_GetChartData_Monthly_CustomWithFromDate() {
        // Arrange
        ZonedDateTime fromZdt = ZonedDateTime.of(2026, 3, 1, 0, 0, 0, 0, SystemZoneId.HO_CHI_MINH_ZONE_ID);
        OpenAiCostQueryCommand command = new OpenAiCostQueryCommand("Custom", "Monthly", fromZdt, null);
        List<OpenAiCostPointResult> mockPoints = List.of(
                new OpenAiCostPointResult("2026-03", new BigDecimal("130.00"), "USD")
        );

        when(openAiCostRepositoryPort.findMonthlyCostsSummary(any(LocalDate.class)))
                .thenReturn(mockPoints);

        // Act
        OpenAiCostChartResult result = openAiCostUseCase.getChartData(command);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("130.00"), result.getTotalCost());
        assertEquals("Monthly", result.getGranularity());
        assertEquals(1, result.getPoints().size());
        verify(openAiCostRepositoryPort, times(1)).findMonthlyCostsSummary(any(LocalDate.class));
    }

    @Test
    @DisplayName("UTCID07 - Lấy biểu đồ chi phí OpenAI theo tháng với khung thời gian mặc định Last6Months")
    void UTCID07_GetChartData_Monthly_Last6Months_Default() {
        // Arrange
        OpenAiCostQueryCommand command = new OpenAiCostQueryCommand("Last6Months", "Monthly", null, null);
        List<OpenAiCostPointResult> mockPoints = List.of(
                new OpenAiCostPointResult("2026-03", new BigDecimal("100.00"), "USD"),
                new OpenAiCostPointResult("2026-04", null, "USD"),
                new OpenAiCostPointResult("2026-05", new BigDecimal("150.00"), "USD")
        );

        when(openAiCostRepositoryPort.findMonthlyCostsSummary(any(LocalDate.class)))
                .thenReturn(mockPoints);

        // Act
        OpenAiCostChartResult result = openAiCostUseCase.getChartData(command);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("250.00"), result.getTotalCost());
        assertEquals("Monthly", result.getGranularity());
        assertEquals(3, result.getPoints().size());
        verify(openAiCostRepositoryPort, times(1)).findMonthlyCostsSummary(any(LocalDate.class));
    }

    @Test
    @DisplayName("UTCID08 - Lấy biểu đồ chi phí OpenAI khi command null mặc định Last6Months và Monthly")
    void UTCID08_GetChartData_NullCommand_DefaultMonthly() {
        // Arrange
        when(openAiCostRepositoryPort.findMonthlyCostsSummary(any(LocalDate.class)))
                .thenReturn(List.of());

        // Act
        OpenAiCostChartResult result = openAiCostUseCase.getChartData(null);

        // Assert
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getTotalCost());
        assertEquals("USD", result.getCurrency());
        assertEquals("Monthly", result.getGranularity());
        assertTrue(result.getPoints().isEmpty());
        verify(openAiCostRepositoryPort, times(1)).findMonthlyCostsSummary(any(LocalDate.class));
    }
}
