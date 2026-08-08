package org.naho.daily.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.daily.command.EarnDailyMissionCommand;
import org.naho.daily.exception.DailyMissionDomainErrorCode;
import org.naho.daily.exception.UserDailyMissionDomainErrorCode;
import org.naho.daily.mapper.UserDailyMissionResultMapper;
import org.naho.daily.model.DailyMission;
import org.naho.daily.model.UserDailyMission;
import org.naho.daily.port.out.DailyMissionRepositoryPort;
import org.naho.daily.port.out.UserDailyMissionRepositoryPort;
import org.naho.daily.result.UserDailyMissionResult;
import org.naho.daily.type.MissionStatus;
import org.naho.daily.type.MissionType;
import org.naho.i18n.message.daily.DailyMissionDetailMessageKey;
import org.naho.i18n.message.daily.UserDailyMissionDetailMessageKey;
import org.naho.i18n.message.learning.UserLearningProgressDetailMessageKey;
import org.naho.learning.exception.UserLearningProgressErrorCode;
import org.naho.learning.model.UserLearningProgress;
import org.naho.learning.port.out.UserLearningProgressRepositoryPort;
import org.naho.point.port.in.CrudPointHistoryInputPort;
import org.naho.shared.constant.SystemZoneId;
import org.naho.shared.exception.ApplicationException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EarnDailyMissionTest {

    @Mock
    private UserDailyMissionRepositoryPort userDailyMissionRepositoryPort;

    @Mock
    private UserDailyMissionResultMapper userDailyMissionResultMapper;

    @Mock
    private UserLearningProgressRepositoryPort userLearningProgressRepositoryPort;

    @Mock
    private DailyMissionRepositoryPort dailyMissionRepositoryPort;

    @Mock
    private CrudPointHistoryInputPort crudPointHistoryInputPort;

    @InjectMocks
    private CrudUserDailyMissionUseCase crudUserDailyMissionUseCase;

    @Test
    @DisplayName("UTCID01 - Nhận thưởng nhiệm vụ hằng ngày thành công")
    void UTCID01_EarnMissionSuccess() {
        // Arrange
        Long userDailyMissionId = 100L;
        Long userId = 1L;
        Long dailyMissionId = 10L;
        LocalDate today = LocalDate.now(SystemZoneId.HO_CHI_MINH_ZONE_ID);

        EarnDailyMissionCommand command = new EarnDailyMissionCommand(userDailyMissionId, userId);

        UserDailyMission mission = UserDailyMission.builder()
                .id(userDailyMissionId)
                .userId(userId)
                .dailyMissionId(dailyMissionId)
                .status(MissionStatus.COMPLETED)
                .startedDate(today)
                .build();

        DailyMission dailyMission = DailyMission.builder()
                .id(dailyMissionId)
                .point(20.0)
                .missionType(MissionType.TALK_WITH_AI)
                .build();

        UserLearningProgress progress = mock(UserLearningProgress.class);
        UserDailyMission savedMission = mock(UserDailyMission.class);
        UserDailyMissionResult expectedResult = mock(UserDailyMissionResult.class);

        when(userDailyMissionRepositoryPort.findByIdAndUserId(userDailyMissionId, userId))
                .thenReturn(Optional.of(mission));
        when(userDailyMissionRepositoryPort.save(mission)).thenReturn(savedMission);
        when(dailyMissionRepositoryPort.findById(dailyMissionId)).thenReturn(Optional.of(dailyMission));
        when(userLearningProgressRepositoryPort.findByUserId(userId)).thenReturn(Optional.of(progress));
        when(userDailyMissionResultMapper.domainToResult(savedMission)).thenReturn(expectedResult);

        // Act
        UserDailyMissionResult result = crudUserDailyMissionUseCase.earnMission(command);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResult, result);
        assertEquals(MissionStatus.EARNED, mission.getStatus());
        assertEquals(today, mission.getEarnedDate());

        verify(progress, times(1)).addPoint(20.0);
        verify(userLearningProgressRepositoryPort, times(1)).save(progress);
        verify(crudPointHistoryInputPort, times(1)).createPointHistory(any());
    }

    @Test
    @DisplayName("UTCID02 - Nhận thưởng thất bại khi không tìm thấy nhiệm vụ người dùng")
    void UTCID02_UserDailyMissionNotFound() {
        // Arrange
        EarnDailyMissionCommand command = new EarnDailyMissionCommand(999L, 1L);
        when(userDailyMissionRepositoryPort.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> crudUserDailyMissionUseCase.earnMission(command)
        );

        assertEquals(UserDailyMissionDomainErrorCode.USER_DAILY_MISSION_NOT_FOUND, exception.getErrorCode());
        assertEquals(UserDailyMissionDetailMessageKey.USER_DAILY_MISSION_NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("UTCID03 - Nhận thưởng thất bại khi nhiệm vụ chưa hoàn thành")
    void UTCID03_MissionNotInProgressState() {
        // Arrange
        Long userDailyMissionId = 100L;
        Long userId = 1L;

        EarnDailyMissionCommand command = new EarnDailyMissionCommand(userDailyMissionId, userId);
        UserDailyMission mission = UserDailyMission.builder()
                .id(userDailyMissionId)
                .userId(userId)
                .dailyMissionId(10L)
                .status(MissionStatus.IN_PROGRESS)
                .build();

        when(userDailyMissionRepositoryPort.findByIdAndUserId(userDailyMissionId, userId))
                .thenReturn(Optional.of(mission));

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> crudUserDailyMissionUseCase.earnMission(command)
        );

        assertEquals(UserDailyMissionDomainErrorCode.USER_DAILY_MISSION_NOT_COMPLETED, exception.getErrorCode());
        assertEquals(UserDailyMissionDetailMessageKey.USER_DAILY_MISSION_NOT_COMPLETED, exception.getMessage());
    }

    @Test
    @DisplayName("UTCID04 - Nhận thưởng thất bại khi nhiệm vụ đã được nhận thưởng trước đó")
    void UTCID04_MissionAlreadyEarned() {
        // Arrange
        Long userDailyMissionId = 100L;
        Long userId = 1L;

        EarnDailyMissionCommand command = new EarnDailyMissionCommand(userDailyMissionId, userId);
        UserDailyMission mission = UserDailyMission.builder()
                .id(userDailyMissionId)
                .userId(userId)
                .dailyMissionId(10L)
                .status(MissionStatus.EARNED)
                .build();

        when(userDailyMissionRepositoryPort.findByIdAndUserId(userDailyMissionId, userId))
                .thenReturn(Optional.of(mission));

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> crudUserDailyMissionUseCase.earnMission(command)
        );

        assertEquals(UserDailyMissionDomainErrorCode.USER_DAILY_MISSION_ALREADY_EARNED, exception.getErrorCode());
        assertEquals(UserDailyMissionDetailMessageKey.USER_DAILY_MISSION_ALREADY_EARNED, exception.getMessage());
    }

    @Test
    @DisplayName("UTCID05 - Nhận thưởng thất bại khi không tìm thấy thông tin cấu hình nhiệm vụ hằng ngày")
    void UTCID05_DailyMissionNotFound() {
        // Arrange
        Long userDailyMissionId = 100L;
        Long userId = 1L;
        Long dailyMissionId = 10L;

        EarnDailyMissionCommand command = new EarnDailyMissionCommand(userDailyMissionId, userId);
        UserDailyMission mission = UserDailyMission.builder()
                .id(userDailyMissionId)
                .userId(userId)
                .dailyMissionId(dailyMissionId)
                .status(MissionStatus.COMPLETED)
                .build();

        when(userDailyMissionRepositoryPort.findByIdAndUserId(userDailyMissionId, userId))
                .thenReturn(Optional.of(mission));
        when(dailyMissionRepositoryPort.findById(dailyMissionId)).thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> crudUserDailyMissionUseCase.earnMission(command)
        );

        assertEquals(DailyMissionDomainErrorCode.DAILY_MISSION_NOT_FOUND, exception.getErrorCode());
        assertEquals(DailyMissionDetailMessageKey.DAILY_MISSION_NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("UTCID06 - Nhận thưởng thất bại khi không tìm thấy tiến trình học tập của người dùng")
    void UTCID06_UserLearningProgressNotFound() {
        // Arrange
        Long userDailyMissionId = 100L;
        Long userId = 1L;
        Long dailyMissionId = 10L;

        EarnDailyMissionCommand command = new EarnDailyMissionCommand(userDailyMissionId, userId);
        UserDailyMission mission = UserDailyMission.builder()
                .id(userDailyMissionId)
                .userId(userId)
                .dailyMissionId(dailyMissionId)
                .status(MissionStatus.COMPLETED)
                .build();

        DailyMission dailyMission = DailyMission.builder()
                .id(dailyMissionId)
                .point(20.0)
                .missionType(MissionType.TALK_WITH_AI)
                .build();

        when(userDailyMissionRepositoryPort.findByIdAndUserId(userDailyMissionId, userId))
                .thenReturn(Optional.of(mission));
        when(dailyMissionRepositoryPort.findById(dailyMissionId)).thenReturn(Optional.of(dailyMission));
        when(userLearningProgressRepositoryPort.findByUserId(userId)).thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> crudUserDailyMissionUseCase.earnMission(command)
        );

        assertEquals(UserLearningProgressErrorCode.USER_LEARNING_PROGRESS_NOT_FOUND, exception.getErrorCode());
        assertEquals(UserLearningProgressDetailMessageKey.USER_LEARNING_PROGRESS_NOT_FOUND_BY_USER_ID, exception.getMessage());
    }
}
