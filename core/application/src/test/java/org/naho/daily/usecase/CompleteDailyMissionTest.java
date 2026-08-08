package org.naho.daily.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.daily.command.CompleteDailyMissionCommand;
import org.naho.daily.exception.UserDailyMissionDomainErrorCode;
import org.naho.daily.mapper.UserDailyMissionResultMapper;
import org.naho.daily.model.UserDailyMission;
import org.naho.daily.port.out.UserDailyMissionRepositoryPort;
import org.naho.daily.result.UserDailyMissionResult;
import org.naho.daily.type.MissionStatus;
import org.naho.daily.type.MissionType;
import org.naho.i18n.message.daily.UserDailyMissionDetailMessageKey;
import org.naho.shared.constant.SystemZoneId;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompleteDailyMissionTest {

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
    @DisplayName("UTCID01 - Hoàn thành nhiệm vụ hằng ngày thành công")
    void UTCID01_CompleteMissionSuccess() {
        // Arrange
        Long userId = 1L;
        MissionType missionType = MissionType.TALK_WITH_AI;
        LocalDate today = LocalDate.now(SystemZoneId.HO_CHI_MINH_ZONE_ID);

        CompleteDailyMissionCommand command = new CompleteDailyMissionCommand(userId, missionType);

        UserDailyMission m1 = UserDailyMission.builder().userId(userId).dailyMissionId(1L).status(MissionStatus.IN_PROGRESS).startedDate(today).build();
        UserDailyMission m2 = UserDailyMission.builder().userId(userId).dailyMissionId(2L).status(MissionStatus.IN_PROGRESS).startedDate(today).build();
        when(userDailyMissionRepositoryPort.findAllByUser_IdAndStartedDate(userId, today))
                .thenReturn(List.of(m1, m2));

        UserDailyMission mission = UserDailyMission.builder()
                .id(100L)
                .userId(userId)
                .dailyMissionId(1L)
                .status(MissionStatus.IN_PROGRESS)
                .startedDate(today)
                .build();

        UserDailyMission savedMission = mock(UserDailyMission.class);
        UserDailyMissionResult expectedResult = mock(UserDailyMissionResult.class);

        when(userDailyMissionRepositoryPort.findByUserIdAndDailyMissionMissionTypeAndStartedDate(userId, missionType, today))
                .thenReturn(Optional.of(mission));
        when(userDailyMissionRepositoryPort.save(mission)).thenReturn(savedMission);
        when(userDailyMissionResultMapper.domainToResult(any())).thenReturn(expectedResult);

        // Act
        UserDailyMissionResult result = crudUserDailyMissionUseCase.completeMission(command);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResult, result);
        assertEquals(MissionStatus.COMPLETED, mission.getStatus());
        assertEquals(today, mission.getCompletedDate());
        verify(userDailyMissionRepositoryPort, times(1)).save(mission);
    }

    @Test
    @DisplayName("UTCID02 - Hoàn thành nhiệm vụ thất bại khi không tìm thấy nhiệm vụ")
    void UTCID02_MissionNotFound() {
        // Arrange
        Long userId = 1L;
        MissionType missionType = MissionType.TALK_WITH_AI;
        LocalDate today = LocalDate.now(SystemZoneId.HO_CHI_MINH_ZONE_ID);

        CompleteDailyMissionCommand command = new CompleteDailyMissionCommand(userId, missionType);

        UserDailyMission m1 = UserDailyMission.builder().userId(userId).dailyMissionId(1L).status(MissionStatus.IN_PROGRESS).startedDate(today).build();
        UserDailyMission m2 = UserDailyMission.builder().userId(userId).dailyMissionId(2L).status(MissionStatus.IN_PROGRESS).startedDate(today).build();
        when(userDailyMissionRepositoryPort.findAllByUser_IdAndStartedDate(userId, today))
                .thenReturn(List.of(m1, m2));

        when(userDailyMissionRepositoryPort.findByUserIdAndDailyMissionMissionTypeAndStartedDate(userId, missionType, today))
                .thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> crudUserDailyMissionUseCase.completeMission(command)
        );

        assertEquals(UserDailyMissionDomainErrorCode.USER_DAILY_MISSION_NOT_FOUND, exception.getErrorCode());
        assertEquals(UserDailyMissionDetailMessageKey.USER_DAILY_MISSION_NOT_FOUND, exception.getMessage());
        verify(userDailyMissionRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("UTCID03 - Hoàn thành nhiệm vụ giữ nguyên khi nhiệm vụ đã hoàn thành hoặc đã nhận thưởng trước đó")
    void UTCID03_MissionAlreadyCompletedOrEarned() {
        // Arrange
        Long userId = 1L;
        MissionType missionType = MissionType.TALK_WITH_AI;
        LocalDate today = LocalDate.now(SystemZoneId.HO_CHI_MINH_ZONE_ID);

        CompleteDailyMissionCommand command = new CompleteDailyMissionCommand(userId, missionType);

        UserDailyMission m1 = UserDailyMission.builder().userId(userId).dailyMissionId(1L).status(MissionStatus.IN_PROGRESS).startedDate(today).build();
        UserDailyMission m2 = UserDailyMission.builder().userId(userId).dailyMissionId(2L).status(MissionStatus.IN_PROGRESS).startedDate(today).build();
        when(userDailyMissionRepositoryPort.findAllByUser_IdAndStartedDate(userId, today))
                .thenReturn(List.of(m1, m2));

        UserDailyMission mission = UserDailyMission.builder()
                .id(100L)
                .userId(userId)
                .dailyMissionId(1L)
                .status(MissionStatus.COMPLETED)
                .startedDate(today)
                .build();
        UserDailyMissionResult expectedResult = mock(UserDailyMissionResult.class);

        when(userDailyMissionRepositoryPort.findByUserIdAndDailyMissionMissionTypeAndStartedDate(userId, missionType, today))
                .thenReturn(Optional.of(mission));
        when(userDailyMissionResultMapper.domainToResult(any())).thenReturn(expectedResult);

        // Act
        UserDailyMissionResult result = crudUserDailyMissionUseCase.completeMission(command);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResult, result);
        verify(userDailyMissionRepositoryPort, never()).save(any());
    }
}
