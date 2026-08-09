package org.naho.daily.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.daily.mapper.UserDailyAttendanceResultMapper;
import org.naho.daily.model.UserDailyAttendance;
import org.naho.daily.port.out.UserDailyAttendanceRepositoryPort;
import org.naho.daily.result.UserDailyAttendanceResult;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindAllUserDailyAttendanceOfCurrentMonthTest {

    @Mock
    private UserDailyAttendanceRepositoryPort userDailyAttendanceRepositoryPort;

    @Mock
    private UserDailyAttendanceResultMapper userDailyAttendanceResultMapper;

    @InjectMocks
    private CrudUserDailyAttendanceUseCase crudUserDailyAttendanceUseCase;

    @Test
    @DisplayName("UTCID01 - Lấy danh sách điểm danh hằng ngày trong tháng hiện tại thành công")
    void UTCID01_FindAllUserDailyAttendanceSuccess() {
        // Arrange
        Long userId = 1L;
        UserDailyAttendance attendance = mock(UserDailyAttendance.class);
        List<UserDailyAttendance> attendanceList = List.of(attendance);
        UserDailyAttendanceResult expectedResult = mock(UserDailyAttendanceResult.class);

        when(userDailyAttendanceRepositoryPort.findAllByUser_IdAndAttendanceDateBetween(eq(userId), any(), any()))
                .thenReturn(attendanceList);
        when(userDailyAttendanceResultMapper.domainToResult(attendance))
                .thenReturn(expectedResult);

        // Act
        List<UserDailyAttendanceResult> results = crudUserDailyAttendanceUseCase.findAllUserDailyAttendanceOfCurrentMonth(userId);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(expectedResult, results.get(0));
        verify(userDailyAttendanceRepositoryPort, times(1)).findAllByUser_IdAndAttendanceDateBetween(eq(userId), any(), any());
    }

    @Test
    @DisplayName("UTCID02 - Lấy danh sách điểm danh hằng ngày trong tháng hiện tại rỗng khi chưa có điểm danh")
    void UTCID02_FindAllUserDailyAttendanceEmpty() {
        // Arrange
        Long userId = 1L;
        when(userDailyAttendanceRepositoryPort.findAllByUser_IdAndAttendanceDateBetween(eq(userId), any(), any()))
                .thenReturn(Collections.emptyList());

        // Act
        List<UserDailyAttendanceResult> results = crudUserDailyAttendanceUseCase.findAllUserDailyAttendanceOfCurrentMonth(userId);

        // Assert
        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(userDailyAttendanceRepositoryPort, times(1)).findAllByUser_IdAndAttendanceDateBetween(eq(userId), any(), any());
    }
}
