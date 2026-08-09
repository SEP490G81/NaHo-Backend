package org.naho.point.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.pagination.PageData;
import org.naho.pagination.PageMeta;
import org.naho.point.command.PointHistoryQueryCommand;
import org.naho.point.mapper.PointHistoryCommandMapper;
import org.naho.point.mapper.PointHistoryResultMapper;
import org.naho.point.model.PointHistory;
import org.naho.point.port.out.PointHistoryRepositoryPort;
import org.naho.point.result.PointHistoryResult;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindAllByUserIdTest {

    @Mock
    private PointHistoryCommandMapper pointHistoryCommandMapper;

    @Mock
    private PointHistoryResultMapper pointHistoryResultMapper;

    @Mock
    private PointHistoryRepositoryPort pointHistoryRepositoryPort;

    @InjectMocks
    private CrudPointHistoryUseCase crudPointHistoryUseCase;

    @Test
    @DisplayName("UTCID01 - Lấy danh sách lịch sử điểm theo userId thành công khi có dữ liệu")
    void UTCID01_FindAllByUserIdSuccess() {
        // Arrange
        Long userId = 1L;
        PointHistoryQueryCommand command = mock(PointHistoryQueryCommand.class);
        PointHistory history1 = mock(PointHistory.class);
        PointHistory history2 = mock(PointHistory.class);
        PointHistoryResult result1 = mock(PointHistoryResult.class);
        PointHistoryResult result2 = mock(PointHistoryResult.class);
        PageMeta pageMeta = mock(PageMeta.class);

        PageData<PointHistory> pageData = PageData.<PointHistory>builder()
                .pageMeta(pageMeta)
                .data(List.of(history1, history2))
                .build();

        when(pointHistoryRepositoryPort.findAllByUserId(command, userId)).thenReturn(pageData);
        when(pointHistoryResultMapper.domainToResult(history1)).thenReturn(result1);
        when(pointHistoryResultMapper.domainToResult(history2)).thenReturn(result2);

        // Act
        PageData<PointHistoryResult> result = crudPointHistoryUseCase.findAllByUserId(command, userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getData().size());
        assertEquals(result1, result.getData().get(0));
        assertEquals(result2, result.getData().get(1));
        assertEquals(pageMeta, result.getPageMeta());

        verify(pointHistoryRepositoryPort, times(1)).findAllByUserId(command, userId);
        verify(pointHistoryResultMapper, times(1)).domainToResult(history1);
        verify(pointHistoryResultMapper, times(1)).domainToResult(history2);
    }

    @Test
    @DisplayName("UTCID02 - Lấy danh sách lịch sử điểm theo userId thành công (danh sách rỗng) khi chưa có dữ liệu")
    void UTCID02_FindAllByUserIdEmpty() {
        // Arrange
        Long userId = 99L;
        PointHistoryQueryCommand command = mock(PointHistoryQueryCommand.class);
        PageMeta pageMeta = mock(PageMeta.class);

        PageData<PointHistory> pageData = PageData.<PointHistory>builder()
                .pageMeta(pageMeta)
                .data(Collections.emptyList())
                .build();

        when(pointHistoryRepositoryPort.findAllByUserId(command, userId)).thenReturn(pageData);

        // Act
        PageData<PointHistoryResult> result = crudPointHistoryUseCase.findAllByUserId(command, userId);

        // Assert
        assertNotNull(result);
        assertTrue(result.getData().isEmpty());
        assertEquals(pageMeta, result.getPageMeta());

        verify(pointHistoryRepositoryPort, times(1)).findAllByUserId(command, userId);
        verify(pointHistoryResultMapper, never()).domainToResult(any());
    }
}
