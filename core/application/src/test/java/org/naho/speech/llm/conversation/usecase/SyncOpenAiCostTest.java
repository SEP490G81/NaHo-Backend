package org.naho.speech.llm.conversation.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.cost.model.OpenAiDailyCost;
import org.naho.cost.port.out.OpenAiCostManagementPort;
import org.naho.cost.port.out.OpenAiCostRepositoryPort;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SyncOpenAiCostTest {

    @Mock
    private OpenAiCostManagementPort openAiCostManagementPort;
    @Mock
    private OpenAiCostRepositoryPort openAiCostRepositoryPort;

    @InjectMocks
    private SyncOpenAiCostUseCase syncOpenAiCostUseCase;

    @Test
    @DisplayName("UTCID01 - Sync backfill ban đầu khi DB rỗng")
    void UTCID01_SyncInitialBackfillIfEmpty_WhenDbEmpty() {
        when(openAiCostRepositoryPort.count()).thenReturn(0L);
        OpenAiDailyCost cost = mock(OpenAiDailyCost.class);
        when(openAiCostManagementPort.fetchDailyCostsFromOpenAi(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(cost));

        syncOpenAiCostUseCase.syncInitialBackfillIfEmpty();

        verify(openAiCostRepositoryPort, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("UTCID02 - Bỏ qua backfill ban đầu khi DB đã có dữ liệu")
    void UTCID02_SyncInitialBackfillIfEmpty_WhenDbNotEmpty() {
        when(openAiCostRepositoryPort.count()).thenReturn(10L);

        syncOpenAiCostUseCase.syncInitialBackfillIfEmpty();

        verify(openAiCostManagementPort, never()).fetchDailyCostsFromOpenAi(any(), any());
    }

    @Test
    @DisplayName("UTCID03 - Sync theo ngày (incremental)")
    void UTCID03_SyncIncremental() {
        OpenAiDailyCost cost = mock(OpenAiDailyCost.class);
        when(openAiCostManagementPort.fetchDailyCostsFromOpenAi(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(cost));

        syncOpenAiCostUseCase.syncIncremental(3);

        verify(openAiCostRepositoryPort, times(1)).saveAll(anyList());
    }
}
