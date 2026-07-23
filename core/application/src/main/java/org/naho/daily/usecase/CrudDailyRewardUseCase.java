package org.naho.daily.usecase;

import org.naho.chest.mapper.ChestResultMapper;
import org.naho.chest.port.out.ChestRepositoryPort;
import org.naho.daily.exception.DailyRewardErrorCode;
import org.naho.daily.mapper.DailyRewardResultMapper;
import org.naho.daily.model.DailyReward;
import org.naho.daily.port.in.CrudDailyRewardInputPort;
import org.naho.daily.port.out.DailyRewardRepositoryPort;
import org.naho.daily.result.DailyRewardResult;
import org.naho.daily.valueobject.RewardYearMonth;
import org.naho.i18n.message.daily.DailyRewardDetailMessageKey;
import org.naho.shared.constant.SystemZoneId;
import org.naho.shared.exception.ApplicationException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class CrudDailyRewardUseCase implements CrudDailyRewardInputPort {

    private final DailyRewardRepositoryPort dailyRewardRepositoryPort;
    private final DailyRewardResultMapper dailyRewardResultMapper;
    private final ChestRepositoryPort chestRepositoryPort;
    private final ChestResultMapper chestResultMapper;

    public CrudDailyRewardUseCase(
            DailyRewardRepositoryPort dailyRewardRepositoryPort,
            DailyRewardResultMapper dailyRewardResultMapper,
            ChestRepositoryPort chestRepositoryPort,
            ChestResultMapper chestResultMapper
    ) {
        this.dailyRewardRepositoryPort = dailyRewardRepositoryPort;
        this.dailyRewardResultMapper = dailyRewardResultMapper;
        this.chestRepositoryPort = chestRepositoryPort;
        this.chestResultMapper = chestResultMapper;
    }

    @Override
    public List<DailyRewardResult> getCurrentMonthDailyRewards() {
        String rewardYearMonth = RewardYearMonth
                .of(YearMonth.now(SystemZoneId.HO_CHI_MINH_ZONE_ID))
                .getValue();

        List<DailyReward> dailyRewards =
                dailyRewardRepositoryPort.findAllByRewardYearMonthOrderByDayOfMonth(rewardYearMonth);

        return dailyRewardResultMapper.domainListToResultList(dailyRewards);
    }

    @Override
    public List<DailyRewardResult> createCurrentMonthDailyRewards() {
        YearMonth yearMonth = YearMonth.now(SystemZoneId.HO_CHI_MINH_ZONE_ID);
        RewardYearMonth rewardYearMonth = RewardYearMonth.of(yearMonth);

        if (dailyRewardRepositoryPort.existsByRewardYearMonth(rewardYearMonth.getValue())) {
            throw new ApplicationException(
                    DailyRewardErrorCode.DAILY_REWARD_ALREADY_EXISTS,
                    DailyRewardDetailMessageKey.DAILY_REWARD_ALREADY_EXISTS,
                    rewardYearMonth.getValue()
            );
        }

        int daysInMonth = yearMonth.lengthOfMonth();
        List<DailyReward> dailyRewards = new ArrayList<>();

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate localDate = yearMonth.atDay(day);

            DailyReward dailyReward = DailyReward.builder()
                    .rewardYearMonth(rewardYearMonth)
                    .dayOfMonth(day)
                    .build();

            if (day == daysInMonth) { // nếu là cuối tháng
                dailyReward.setChestId(4L);
            } else if (day == 15) { // nếu là ngày 15 hàng tháng
                dailyReward.setChestId(3L);
            } else if (localDate.getDayOfWeek() == DayOfWeek.SUNDAY) { // nếu là Chủ Nhật
                dailyReward.setChestId(2L);
            } else { // các ngày còn lại
                dailyReward.setChestId(1L);
            }

            dailyRewards.add(dailyReward);
        }

        List<DailyReward> savedDailyRewards = dailyRewardRepositoryPort.saveAll(dailyRewards);
        return dailyRewardResultMapper.domainListToResultList(savedDailyRewards);
    }
}
