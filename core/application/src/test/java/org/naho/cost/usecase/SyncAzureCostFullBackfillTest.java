package org.naho.cost.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.cost.model.AzureDailyCost;
import org.naho.cost.port.out.AzureCostManagementPort;
import org.naho.cost.port.out.AzureCostRepositoryPort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SyncAzureCostFullBackfillTest {

    @Mock
    private AzureCostManagementPort azureCostManagementPort;

    @Mock
    private AzureCostRepositoryPort azureCostRepositoryPort;

    @InjectMocks
    private SyncAzureCostUseCase syncAzureCostUseCase;

    @Test
    @DisplayName("UTCID01 - Đồng bộ toàn bộ dữ liệu chi phí Azure thành công khi có startDate")
    void UTCID01_SyncFullBackfill_WithStartDate_Success() {
        // Arrange
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        List<AzureDailyCost> fetchedList = List.of(
                AzureDailyCost.builder()
                        .id(1L)
                        .recordDate(LocalDate.of(2026, 1, 1))
                        .costAmount(new BigDecimal("10.00"))
                        .currency("USD")
                        .build()
        );

        when(azureCostManagementPort.fetchDailyCostsFromAzure(eq(startDate), any(LocalDate.class)))
                .thenReturn(fetchedList);

        // Act
        assertDoesNotThrow(() -> syncAzureCostUseCase.syncFullBackfill(startDate));

        // Assert
        verify(azureCostManagementPort, times(1)).fetchDailyCostsFromAzure(eq(startDate), any(LocalDate.class));
        verify(azureCostRepositoryPort, times(1)).saveAll(fetchedList);
    }

    @Test
    @DisplayName("UTCID02 - Đồng bộ toàn bộ dữ liệu chi phí Azure mặc định từ 6 tháng trước khi startDate là null")
    void UTCID02_SyncFullBackfill_NullStartDate_DefaultsToSixMonthsAgo() {
        // Arrange
        List<AzureDailyCost> fetchedList = List.of(
                AzureDailyCost.builder()
                        .id(1L)
                        .recordDate(LocalDate.of(2026, 2, 1))
                        .costAmount(new BigDecimal("15.00"))
                        .currency("USD")
                        .build()
        );

        when(azureCostManagementPort.fetchDailyCostsFromAzure(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(fetchedList);

        // Act
        assertDoesNotThrow(() -> syncAzureCostUseCase.syncFullBackfill(null));

        // Assert
        verify(azureCostManagementPort, times(1)).fetchDailyCostsFromAzure(any(LocalDate.class), any(LocalDate.class));
        verify(azureCostRepositoryPort, times(1)).saveAll(fetchedList);
    }

    @Test
    @DisplayName("UTCID03 - Đồng bộ toàn bộ dữ liệu chi phí Azure không lưu vào DB khi kết quả trả về rỗng")
    void UTCID03_SyncFullBackfill_EmptyFetchedCosts_NoSave() {
        // Arrange
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        when(azureCostManagementPort.fetchDailyCostsFromAzure(eq(startDate), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        // Act
        assertDoesNotThrow(() -> syncAzureCostUseCase.syncFullBackfill(startDate));

        // Assert
        verify(azureCostManagementPort, times(1)).fetchDailyCostsFromAzure(eq(startDate), any(LocalDate.class));
        verify(azureCostRepositoryPort, never()).saveAll(any());
    }
}
