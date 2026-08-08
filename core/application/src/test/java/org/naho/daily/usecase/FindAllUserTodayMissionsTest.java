package org.naho.daily.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.daily.exception.UserDailyMissionDomainErrorCode;
import org.naho.daily.mapper.UserDailyMissionResultMapper;
import org.naho.daily.model.UserDailyMission;
import org.naho.daily.port.out.UserDailyMissionRepositoryPort;
import org.naho.daily.result.UserDailyMissionResult;
import org.naho.daily.type.MissionStatus;
import org.naho.i18n.message.daily.UserDailyMissionDetailMessageKey;
import org.naho.shared.constant.SystemZoneId;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindAllUserTodayMissionsTest {

    @Mock
    private UserDailyMissionRepositoryPort userDailyMissionRepositoryPort;

    @Mock
    private UserDailyMissionResultMapper userDailyMissionResultMapper;

    @Mock
    private TransactionPort transactionPort;

    @InjectMocks
    private CrudUserDailyMissionUseCase crudUserDailyMissionUseCase;

    @BeforeEach
    void setUp() {
        lenient().when(transactionPort.execute(any(Supplier.class))).thenAnswer(invocation -> {
            Supplier<?> supplier = invocation.getArgument(0);
            return supplier.get();
        });
    }

    @Test
    @DisplayName("UTCID01 - Lấy danh sách nhiệm vụ hôm nay thành công khi đã có dữ liệu")
    void UTCID01_FindAllUserTodayMissionsExist() {
        // Arrange
        Long userId = 1L;
        LocalDate today = LocalDate.now(SystemZoneId.HO_CHI_MINH_ZONE_ID);

        UserDailyMission m1 = UserDailyMission.builder().userId(userId).dailyMissionId(1L).build();
        UserDailyMission m2 = UserDailyMission.builder().userId(userId).dailyMissionId(2L).build();

        UserDailyMissionResult r1 = mock(UserDailyMissionResult.class);
        UserDailyMissionResult r2 = mock(UserDailyMissionResult.class);

        when(userDailyMissionRepositoryPort.findAllByUser_IdAndStartedDate(userId, today))
                .thenReturn(List.of(m1, m2));
        when(userDailyMissionResultMapper.domainToResult(m1)).thenReturn(r1);
        when(userDailyMissionResultMapper.domainToResult(m2)).thenReturn(r2);

        // Act
        List<UserDailyMissionResult> results = crudUserDailyMissionUseCase.findAllUserTodayMissions(userId);

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        verify(userDailyMissionRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("UTCID02 - Lấy danh sách nhiệm vụ hôm nay thành công và tự khởi tạo khi chưa có nhiệm vụ")
    void UTCID02_FindAllUserTodayMissionsEmpty() {
        // Arrange
        Long userId = 1L;
        LocalDate today = LocalDate.now(SystemZoneId.HO_CHI_MINH_ZONE_ID);

        UserDailyMission m1 = UserDailyMission.builder().userId(userId).dailyMissionId(1L).status(MissionStatus.IN_PROGRESS).startedDate(today).build();
        UserDailyMission m2 = UserDailyMission.builder().userId(userId).dailyMissionId(2L).status(MissionStatus.IN_PROGRESS).startedDate(today).build();

        UserDailyMissionResult r1 = mock(UserDailyMissionResult.class);
        UserDailyMissionResult r2 = mock(UserDailyMissionResult.class);

        when(userDailyMissionRepositoryPort.findAllByUser_IdAndStartedDate(userId, today))
                .thenReturn(Collections.emptyList());
        when(userDailyMissionRepositoryPort.save(any()))
                .thenReturn(m1)
                .thenReturn(m2);
        when(userDailyMissionResultMapper.domainToResult(m1)).thenReturn(r1);
        when(userDailyMissionResultMapper.domainToResult(m2)).thenReturn(r2);

        // Act
        List<UserDailyMissionResult> results = crudUserDailyMissionUseCase.findAllUserTodayMissions(userId);

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        verify(userDailyMissionRepositoryPort, times(2)).save(any());
    }

    @Test
    @DisplayName("UTCID03 - Lấy danh sách nhiệm vụ hôm nay thất bại khi số lượng nhiệm vụ không hợp lệ")
    void UTCID03_FindAllUserTodayMissionsInvalidSize() {
        // Arrange
        Long userId = 1L;
        LocalDate today = LocalDate.now(SystemZoneId.HO_CHI_MINH_ZONE_ID);

        UserDailyMission m1 = UserDailyMission.builder().userId(userId).dailyMissionId(1L).build();

        when(userDailyMissionRepositoryPort.findAllByUser_IdAndStartedDate(userId, today))
                .thenReturn(List.of(m1));

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> crudUserDailyMissionUseCase.findAllUserTodayMissions(userId)
        );

        assertEquals(UserDailyMissionDomainErrorCode.USER_DAILY_MISSION_NOT_FOUND, exception.getErrorCode());
        assertEquals(UserDailyMissionDetailMessageKey.USER_DAILY_MISSION_NOT_FOUND, exception.getMessage());
    }
}
