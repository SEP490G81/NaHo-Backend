package org.naho.daily.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.chest.exception.ChestErrorCode;
import org.naho.chest.model.Chest;
import org.naho.chest.port.out.ChestRepositoryPort;
import org.naho.daily.command.EarnDailyRewardCommand;
import org.naho.daily.exception.DailyRewardErrorCode;
import org.naho.daily.mapper.UserDailyAttendanceResultMapper;
import org.naho.daily.model.DailyReward;
import org.naho.daily.model.UserDailyAttendance;
import org.naho.daily.port.out.DailyRewardRepositoryPort;
import org.naho.daily.port.out.UserDailyAttendanceRepositoryPort;
import org.naho.daily.result.UserDailyAttendanceResult;
import org.naho.i18n.message.chest.ChestDetailMessageKey;
import org.naho.i18n.message.daily.DailyRewardDetailMessageKey;
import org.naho.i18n.message.daily.UserDailyAttendanceDetailMessageKey;
import org.naho.i18n.message.learning.UserLearningProgressDetailMessageKey;
import org.naho.learning.exception.UserLearningProgressErrorCode;
import org.naho.learning.model.UserLearningProgress;
import org.naho.learning.port.in.CrudUserLearningProgressInputPort;
import org.naho.learning.port.out.UserLearningProgressRepositoryPort;
import org.naho.point.port.in.CrudPointHistoryInputPort;
import org.naho.shared.constant.SystemZoneId;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;

import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EarnDailyRewardTest {

    @Mock
    private DailyRewardRepositoryPort dailyRewardRepositoryPort;

    @Mock
    private ChestRepositoryPort chestRepositoryPort;

    @Mock
    private UserLearningProgressRepositoryPort userLearningProgressRepositoryPort;

    @Mock
    private UserDailyAttendanceRepositoryPort userDailyAttendanceRepositoryPort;

    @Mock
    private UserDailyAttendanceResultMapper userDailyAttendanceResultMapper;

    @Mock
    private CrudPointHistoryInputPort crudPointHistoryInputPort;

    @Mock
    private CrudUserLearningProgressInputPort crudUserLearningProgressInputPort;

    @Mock
    private TransactionPort transactionPort;

    @InjectMocks
    private CrudDailyRewardUseCase crudDailyRewardUseCase;

    @BeforeEach
    void setUp() {
        lenient().when(transactionPort.execute(any(Supplier.class))).thenAnswer(invocation -> {
            Supplier<?> supplier = invocation.getArgument(0);
            return supplier.get();
        });
    }

    @Test
    @DisplayName("UTCID01 - Nhận điểm thưởng hằng ngày thành công")
    void UTCID01_EarnDailyRewardSuccess() {
        // Arrange
        Long userId = 10L;
        Long dailyRewardId = 1L;
        LocalDate now = LocalDate.now(SystemZoneId.HO_CHI_MINH_ZONE_ID);

        EarnDailyRewardCommand command = new EarnDailyRewardCommand(userId, dailyRewardId);

        DailyReward dailyReward = mock(DailyReward.class);
        when(dailyReward.getLocalDate()).thenReturn(now);

        UserLearningProgress progress = mock(UserLearningProgress.class);
        Chest chest = mock(Chest.class);
        when(chest.getRandomPoint()).thenReturn(50);

        UserDailyAttendance savedAttendance = mock(UserDailyAttendance.class);
        UserDailyAttendanceResult expectedResult = mock(UserDailyAttendanceResult.class);

        when(dailyRewardRepositoryPort.findById(dailyRewardId)).thenReturn(Optional.of(dailyReward));
        when(userDailyAttendanceRepositoryPort.existsByUser_IdAndAttendanceDate(userId, now)).thenReturn(false);
        when(userLearningProgressRepositoryPort.findByUserId(userId)).thenReturn(Optional.of(progress));
        when(chestRepositoryPort.findByDailyRewardId(dailyRewardId)).thenReturn(Optional.of(chest));
        when(userDailyAttendanceRepositoryPort.save(any())).thenReturn(savedAttendance);
        when(userDailyAttendanceResultMapper.domainToResult(savedAttendance)).thenReturn(expectedResult);

        // Act
        UserDailyAttendanceResult result = crudDailyRewardUseCase.earnDailyReward(command);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResult, result);

        verify(progress, times(1)).addPoint(50.0);
        verify(userLearningProgressRepositoryPort, times(1)).save(progress);
        verify(crudPointHistoryInputPort, times(1)).createPointHistory(any());
        verify(userDailyAttendanceRepositoryPort, times(1)).save(any());
    }

    @Test
    @DisplayName("UTCID02 - Nhận điểm thưởng thất bại khi phần thưởng không tồn tại")
    void UTCID02_DailyRewardNotFound() {
        // Arrange
        EarnDailyRewardCommand command = new EarnDailyRewardCommand(10L, 999L);
        when(dailyRewardRepositoryPort.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> crudDailyRewardUseCase.earnDailyReward(command)
        );

        assertEquals(DailyRewardErrorCode.DAILY_REWARD_NOT_FOUND, exception.getErrorCode());
        assertEquals(DailyRewardDetailMessageKey.DAILY_REWARD_NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("UTCID03 - Nhận điểm thưởng thất bại khi phần thưởng không phải của ngày hôm nay")
    void UTCID03_DailyRewardNotForToday() {
        // Arrange
        Long userId = 10L;
        Long dailyRewardId = 1L;
        LocalDate yesterday = LocalDate.now(SystemZoneId.HO_CHI_MINH_ZONE_ID).minusDays(1);

        EarnDailyRewardCommand command = new EarnDailyRewardCommand(userId, dailyRewardId);
        DailyReward dailyReward = mock(DailyReward.class);
        when(dailyReward.getLocalDate()).thenReturn(yesterday);

        when(dailyRewardRepositoryPort.findById(dailyRewardId)).thenReturn(Optional.of(dailyReward));

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> crudDailyRewardUseCase.earnDailyReward(command)
        );

        assertEquals(DailyRewardErrorCode.DAILY_REWARD_NOT_FOR_TODAY, exception.getErrorCode());
        assertEquals(DailyRewardDetailMessageKey.DAILY_REWARD_NOT_FOR_TODAY, exception.getMessage());
    }

    @Test
    @DisplayName("UTCID04 - Nhận điểm thưởng thất bại khi người dùng đã điểm danh ngày hôm nay rồi")
    void UTCID04_UserDailyAttendanceAlreadyExists() {
        // Arrange
        Long userId = 10L;
        Long dailyRewardId = 1L;
        LocalDate now = LocalDate.now(SystemZoneId.HO_CHI_MINH_ZONE_ID);

        EarnDailyRewardCommand command = new EarnDailyRewardCommand(userId, dailyRewardId);
        DailyReward dailyReward = mock(DailyReward.class);
        when(dailyReward.getLocalDate()).thenReturn(now);

        when(dailyRewardRepositoryPort.findById(dailyRewardId)).thenReturn(Optional.of(dailyReward));
        when(userDailyAttendanceRepositoryPort.existsByUser_IdAndAttendanceDate(userId, now)).thenReturn(true);

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> crudDailyRewardUseCase.earnDailyReward(command)
        );

        assertEquals(DailyRewardErrorCode.USER_DAILY_ATTENDANCE_ALREADY_EXISTS, exception.getErrorCode());
        assertEquals(UserDailyAttendanceDetailMessageKey.USER_DAILY_ATTENDANCE_ALREADY_EXISTS, exception.getMessage());
    }

    @Test
    @DisplayName("UTCID05 - Nhận điểm thưởng thất bại khi không tìm thấy tiến trình học tập của người dùng")
    void UTCID05_UserLearningProgressNotFound() {
        // Arrange
        Long userId = 10L;
        Long dailyRewardId = 1L;
        LocalDate now = LocalDate.now(SystemZoneId.HO_CHI_MINH_ZONE_ID);

        EarnDailyRewardCommand command = new EarnDailyRewardCommand(userId, dailyRewardId);
        DailyReward dailyReward = mock(DailyReward.class);
        when(dailyReward.getLocalDate()).thenReturn(now);

        when(dailyRewardRepositoryPort.findById(dailyRewardId)).thenReturn(Optional.of(dailyReward));
        when(userDailyAttendanceRepositoryPort.existsByUser_IdAndAttendanceDate(userId, now)).thenReturn(false);
        when(userLearningProgressRepositoryPort.findByUserId(userId)).thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> crudDailyRewardUseCase.earnDailyReward(command)
        );

        assertEquals(UserLearningProgressErrorCode.USER_LEARNING_PROGRESS_NOT_FOUND, exception.getErrorCode());
        assertEquals(UserLearningProgressDetailMessageKey.USER_LEARNING_PROGRESS_NOT_FOUND_BY_USER_ID, exception.getMessage());
    }

    @Test
    @DisplayName("UTCID06 - Nhận điểm thưởng thất bại khi không tìm thấy thông tin rương báu")
    void UTCID06_ChestNotFound() {
        // Arrange
        Long userId = 10L;
        Long dailyRewardId = 1L;
        LocalDate now = LocalDate.now(SystemZoneId.HO_CHI_MINH_ZONE_ID);

        EarnDailyRewardCommand command = new EarnDailyRewardCommand(userId, dailyRewardId);
        DailyReward dailyReward = mock(DailyReward.class);
        when(dailyReward.getLocalDate()).thenReturn(now);

        UserLearningProgress progress = mock(UserLearningProgress.class);

        when(dailyRewardRepositoryPort.findById(dailyRewardId)).thenReturn(Optional.of(dailyReward));
        when(userDailyAttendanceRepositoryPort.existsByUser_IdAndAttendanceDate(userId, now)).thenReturn(false);
        when(userLearningProgressRepositoryPort.findByUserId(userId)).thenReturn(Optional.of(progress));
        when(chestRepositoryPort.findByDailyRewardId(dailyRewardId)).thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> crudDailyRewardUseCase.earnDailyReward(command)
        );

        assertEquals(ChestErrorCode.CHEST_NOT_FOUND, exception.getErrorCode());
        assertEquals(ChestDetailMessageKey.CHEST_NOT_FOUND, exception.getMessage());
    }
}
