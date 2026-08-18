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
class SyncAzureCostCustomRangeTest {

    @Mock
    private AzureCostManagementPort azureCostManagementPort;

    @Mock
    private AzureCostRepositoryPort azureCostRepositoryPort;

    @InjectMocks
    private SyncAzureCostUseCase syncAzureCostUseCase;

    @Test
    @DisplayName("UTCID01 - Đồng bộ chi phí Azure theo khoảng ngày tuỳ chỉnh thành công khi có dữ liệu")
    void UTCID01_SyncCustomRange_HasRecords_SaveAllSuccess() {
        // Arrange
        LocalDate fromDate = LocalDate.of(2026, 7, 1);
        LocalDate toDate = LocalDate.of(2026, 7, 31);
        List<AzureDailyCost> fetchedList = List.of(
                AzureDailyCost.builder()
                        .id(1L)
                        .recordDate(LocalDate.of(2026, 7, 15))
                        .costAmount(new BigDecimal("18.50"))
                        .currency("USD")
                        .build()
        );

        when(azureCostManagementPort.fetchDailyCostsFromAzure(fromDate, toDate))
                .thenReturn(fetchedList);

        // Act
        assertDoesNotThrow(() -> syncAzureCostUseCase.syncCustomRange(fromDate, toDate));

        // Assert
        verify(azureCostManagementPort, times(1)).fetchDailyCostsFromAzure(fromDate, toDate);
        verify(azureCostRepositoryPort, times(1)).saveAll(fetchedList);
    }

    @Test
    @DisplayName("UTCID02 - Đồng bộ chi phí Azure không lưu vào DB khi kết quả là danh sách rỗng")
    void UTCID02_SyncCustomRange_EmptyList_DoNotSave() {
        // Arrange
        LocalDate fromDate = LocalDate.of(2026, 7, 1);
        LocalDate toDate = LocalDate.of(2026, 7, 31);

        when(azureCostManagementPort.fetchDailyCostsFromAzure(fromDate, toDate))
                .thenReturn(Collections.emptyList());

        // Act
        assertDoesNotThrow(() -> syncAzureCostUseCase.syncCustomRange(fromDate, toDate));

        // Assert
        verify(azureCostManagementPort, times(1)).fetchDailyCostsFromAzure(fromDate, toDate);
        verify(azureCostRepositoryPort, never()).saveAll(any());
    }

    @Test
    @DisplayName("UTCID03 - Đồng bộ chi phí Azure không lưu vào DB khi kết quả trả về null")
    void UTCID03_SyncCustomRange_NullList_DoNotSave() {
        // Arrange
        LocalDate fromDate = LocalDate.of(2026, 7, 1);
        LocalDate toDate = LocalDate.of(2026, 7, 31);

        when(azureCostManagementPort.fetchDailyCostsFromAzure(fromDate, toDate))
                .thenReturn(null);

        // Act
        assertDoesNotThrow(() -> syncAzureCostUseCase.syncCustomRange(fromDate, toDate));

        // Assert
        verify(azureCostManagementPort, times(1)).fetchDailyCostsFromAzure(fromDate, toDate);
        verify(azureCostRepositoryPort, never()).saveAll(any());
    }
}
