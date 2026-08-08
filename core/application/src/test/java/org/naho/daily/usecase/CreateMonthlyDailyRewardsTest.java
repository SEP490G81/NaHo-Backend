package org.naho.daily.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.daily.exception.DailyRewardErrorCode;
import org.naho.daily.mapper.DailyRewardResultMapper;
import org.naho.daily.model.DailyReward;
import org.naho.daily.port.out.DailyRewardRepositoryPort;
import org.naho.daily.result.DailyRewardResult;
import org.naho.daily.valueobject.RewardYearMonth;
import org.naho.i18n.message.daily.DailyRewardDetailMessageKey;
import org.naho.shared.exception.ApplicationException;

import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateMonthlyDailyRewardsTest {

    @Mock
    private DailyRewardRepositoryPort dailyRewardRepositoryPort;

    @Mock
    private DailyRewardResultMapper dailyRewardResultMapper;

    @InjectMocks
    private CrudDailyRewardUseCase crudDailyRewardUseCase;

    @Test
    @DisplayName("UTCID01 - Tạo danh sách phần thưởng hằng ngày cho tháng thành công")
    void UTCID01_CreateMonthlyDailyRewardsSuccess() {
        // Arrange
        YearMonth yearMonth = YearMonth.of(2026, 8);
        String yearMonthStr = RewardYearMonth.of(yearMonth).getValue();

        when(dailyRewardRepositoryPort.existsByRewardYearMonth(yearMonthStr))
                .thenReturn(false);

        List<DailyReward> savedList = List.of(mock(DailyReward.class));
        DailyRewardResult expectedResult = mock(DailyRewardResult.class);

        when(dailyRewardRepositoryPort.saveAll(any()))
                .thenReturn(savedList);
        when(dailyRewardResultMapper.domainListToResultList(savedList))
                .thenReturn(List.of(expectedResult));

        // Act
        List<DailyRewardResult> results = crudDailyRewardUseCase.createMonthlyDailyRewards(yearMonth);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(expectedResult, results.get(0));
        verify(dailyRewardRepositoryPort, times(1)).existsByRewardYearMonth(yearMonthStr);
        verify(dailyRewardRepositoryPort, times(1)).saveAll(any());
    }

    @Test
    @DisplayName("UTCID02 - Tạo danh sách phần thưởng hằng ngày thất bại khi tháng đã tồn tại")
    void UTCID02_RewardYearMonthAlreadyExists() {
        // Arrange
        YearMonth yearMonth = YearMonth.of(2026, 8);
        String yearMonthStr = RewardYearMonth.of(yearMonth).getValue();

        when(dailyRewardRepositoryPort.existsByRewardYearMonth(yearMonthStr))
                .thenReturn(true);

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> crudDailyRewardUseCase.createMonthlyDailyRewards(yearMonth)
        );

        assertEquals(DailyRewardErrorCode.DAILY_REWARD_ALREADY_EXISTS, exception.getErrorCode());
        assertEquals(DailyRewardDetailMessageKey.DAILY_REWARD_ALREADY_EXISTS, exception.getMessage());
        verify(dailyRewardRepositoryPort, never()).saveAll(any());
    }
}
