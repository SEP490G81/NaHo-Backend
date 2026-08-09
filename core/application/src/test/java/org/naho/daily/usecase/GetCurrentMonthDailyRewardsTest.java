package org.naho.daily.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.daily.mapper.DailyRewardResultMapper;
import org.naho.daily.model.DailyReward;
import org.naho.daily.port.out.DailyRewardRepositoryPort;
import org.naho.daily.result.DailyRewardResult;
import org.naho.daily.valueobject.RewardYearMonth;
import org.naho.shared.constant.SystemZoneId;

import java.time.YearMonth;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetCurrentMonthDailyRewardsTest {

    @Mock
    private DailyRewardRepositoryPort dailyRewardRepositoryPort;

    @Mock
    private DailyRewardResultMapper dailyRewardResultMapper;

    @InjectMocks
    private CrudDailyRewardUseCase crudDailyRewardUseCase;

    @Test
    @DisplayName("UTCID01 - Lấy phần thưởng hằng ngày của tháng hiện tại thành công khi đã tồn tại dữ liệu")
    void UTCID01_GetCurrentMonthDailyRewardsExist() {
        // Arrange
        YearMonth currentYearMonth = YearMonth.now(SystemZoneId.HO_CHI_MINH_ZONE_ID);
        String rewardYearMonth = RewardYearMonth.of(currentYearMonth).getValue();

        DailyReward reward = DailyReward.builder()
                .id(1L)
                .rewardYearMonth(RewardYearMonth.of(currentYearMonth))
                .dayOfMonth(1)
                .chestId(1L)
                .build();
        List<DailyReward> dailyRewards = List.of(reward);

        DailyRewardResult expectedResult = mock(DailyRewardResult.class);
        List<DailyRewardResult> expectedResults = List.of(expectedResult);

        when(dailyRewardRepositoryPort.findAllByRewardYearMonthOrderByDayOfMonth(rewardYearMonth))
                .thenReturn(dailyRewards);
        when(dailyRewardResultMapper.domainListToResultList(dailyRewards))
                .thenReturn(expectedResults);

        // Act
        List<DailyRewardResult> results = crudDailyRewardUseCase.getCurrentMonthDailyRewards();

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(expectedResult, results.get(0));
        verify(dailyRewardRepositoryPort, times(1)).findAllByRewardYearMonthOrderByDayOfMonth(rewardYearMonth);
    }

    @Test
    @DisplayName("UTCID02 - Lấy phần thưởng hằng ngày của tháng hiện tại và tạo mới khi chưa có dữ liệu")
    void UTCID02_GetCurrentMonthDailyRewardsEmpty() {
        // Arrange
        YearMonth currentYearMonth = YearMonth.now(SystemZoneId.HO_CHI_MINH_ZONE_ID);
        String rewardYearMonth = RewardYearMonth.of(currentYearMonth).getValue();

        when(dailyRewardRepositoryPort.findAllByRewardYearMonthOrderByDayOfMonth(rewardYearMonth))
                .thenReturn(Collections.emptyList());
        when(dailyRewardRepositoryPort.existsByRewardYearMonth(rewardYearMonth))
                .thenReturn(false);

        List<DailyReward> savedDailyRewards = List.of(mock(DailyReward.class));
        DailyRewardResult expectedResult = mock(DailyRewardResult.class);
        when(dailyRewardRepositoryPort.saveAll(any()))
                .thenReturn(savedDailyRewards);
        when(dailyRewardResultMapper.domainListToResultList(savedDailyRewards))
                .thenReturn(List.of(expectedResult));

        // Act
        List<DailyRewardResult> results = crudDailyRewardUseCase.getCurrentMonthDailyRewards();

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        verify(dailyRewardRepositoryPort, times(1)).existsByRewardYearMonth(rewardYearMonth);
        verify(dailyRewardRepositoryPort, times(1)).saveAll(any());
    }
}
