package org.naho.point.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.point.command.PointHistoryCommand;
import org.naho.point.mapper.PointHistoryCommandMapper;
import org.naho.point.mapper.PointHistoryResultMapper;
import org.naho.point.model.PointHistory;
import org.naho.point.port.out.PointHistoryRepositoryPort;
import org.naho.point.result.PointHistoryResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreatePointHistoryTest {

    @Mock
    private PointHistoryCommandMapper pointHistoryCommandMapper;

    @Mock
    private PointHistoryResultMapper pointHistoryResultMapper;

    @Mock
    private PointHistoryRepositoryPort pointHistoryRepositoryPort;

    @InjectMocks
    private CrudPointHistoryUseCase crudPointHistoryUseCase;

    @Test
    @DisplayName("UTCID01 - Tạo lịch sử cộng/trừ điểm thành công khi thông tin command hợp lệ")
    void UTCID01_CreatePointHistorySuccess() {
        // Arrange
        PointHistoryCommand command = mock(PointHistoryCommand.class);
        PointHistory pointHistory = mock(PointHistory.class);
        PointHistory savedPointHistory = mock(PointHistory.class);
        PointHistoryResult expectedResult = mock(PointHistoryResult.class);

        when(pointHistoryCommandMapper.commandToDomain(command)).thenReturn(pointHistory);
        when(pointHistoryRepositoryPort.save(pointHistory)).thenReturn(savedPointHistory);
        when(pointHistoryResultMapper.domainToResult(savedPointHistory)).thenReturn(expectedResult);

        // Act
        PointHistoryResult result = crudPointHistoryUseCase.createPointHistory(command);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResult, result);
        verify(pointHistoryCommandMapper, times(1)).commandToDomain(command);
        verify(pointHistoryRepositoryPort, times(1)).save(pointHistory);
        verify(pointHistoryResultMapper, times(1)).domainToResult(savedPointHistory);
    }
}
