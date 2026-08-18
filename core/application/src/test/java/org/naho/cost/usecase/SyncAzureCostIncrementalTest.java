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
class SyncAzureCostIncrementalTest {

    @Mock
    private AzureCostManagementPort azureCostManagementPort;

    @Mock
    private AzureCostRepositoryPort azureCostRepositoryPort;

    @InjectMocks
    private SyncAzureCostUseCase syncAzureCostUseCase;

    @Test
    @DisplayName("UTCID01 - Đồng bộ tăng dần dữ liệu chi phí Azure thành công khi lookbackDays > 0")
    void UTCID01_SyncIncremental_PositiveLookbackDays_Success() {
        // Arrange
        int lookbackDays = 7;
        List<AzureDailyCost> fetchedList = List.of(
                AzureDailyCost.builder()
                        .id(1L)
                        .recordDate(LocalDate.now().minusDays(1))
                        .costAmount(new BigDecimal("20.00"))
                        .currency("USD")
                        .build()
        );

        when(azureCostManagementPort.fetchDailyCostsFromAzure(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(fetchedList);

        // Act
        assertDoesNotThrow(() -> syncAzureCostUseCase.syncIncremental(lookbackDays));

        // Assert
        verify(azureCostManagementPort, times(1)).fetchDailyCostsFromAzure(any(LocalDate.class), any(LocalDate.class));
        verify(azureCostRepositoryPort, times(1)).saveAll(fetchedList);
    }

    @Test
    @DisplayName("UTCID02 - Đồng bộ tăng dần dữ liệu chi phí Azure mặc định 3 ngày khi lookbackDays <= 0")
    void UTCID02_SyncIncremental_ZeroOrNegativeLookbackDays_DefaultsToThreeDays() {
        // Arrange
        int lookbackDays = 0;
        List<AzureDailyCost> fetchedList = List.of(
                AzureDailyCost.builder()
                        .id(1L)
                        .recordDate(LocalDate.now().minusDays(1))
                        .costAmount(new BigDecimal("12.00"))
                        .currency("USD")
                        .build()
        );

        when(azureCostManagementPort.fetchDailyCostsFromAzure(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(fetchedList);

        // Act
        assertDoesNotThrow(() -> syncAzureCostUseCase.syncIncremental(lookbackDays));

        // Assert
        verify(azureCostManagementPort, times(1)).fetchDailyCostsFromAzure(any(LocalDate.class), any(LocalDate.class));
        verify(azureCostRepositoryPort, times(1)).saveAll(fetchedList);
    }

    @Test
    @DisplayName("UTCID03 - Đồng bộ tăng dần dữ liệu chi phí Azure không lưu vào DB khi kết quả trả về rỗng")
    void UTCID03_SyncIncremental_EmptyFetchedCosts_NoSave() {
        // Arrange
        int lookbackDays = 5;
        when(azureCostManagementPort.fetchDailyCostsFromAzure(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        // Act
        assertDoesNotThrow(() -> syncAzureCostUseCase.syncIncremental(lookbackDays));

        // Assert
        verify(azureCostManagementPort, times(1)).fetchDailyCostsFromAzure(any(LocalDate.class), any(LocalDate.class));
        verify(azureCostRepositoryPort, never()).saveAll(any());
    }
}
