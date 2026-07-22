package org.naho.daily.usecase;

import org.naho.chest.mapper.ChestResultMapper;
import org.naho.chest.model.Chest;
import org.naho.chest.port.out.ChestRepositoryPort;
import org.naho.chest.result.ChestResult;
import org.naho.daily.mapper.DailyRewardResultMapper;
import org.naho.daily.model.DailyReward;
import org.naho.daily.port.in.CrudDailyRewardInputPort;
import org.naho.daily.port.out.DailyRewardRepositoryPort;
import org.naho.daily.result.DailyRewardResult;
import org.naho.daily.valueobject.RewardYearMonth;
import org.naho.shared.constant.SystemZoneId;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
        String rewardYearMonth =
                RewardYearMonth.of(
                        YearMonth.now(SystemZoneId.HO_CHI_MINH_ZONE_ID)
                ).getValue();

        List<DailyReward> dailyRewards =
                dailyRewardRepositoryPort.findAllByRewardYearMonthOrderByDayOfMonth(rewardYearMonth);

        // lấy danh sách các chestIds từ dailyRewards
        Set<Long> chestIds = dailyRewards.stream()
                .map(DailyReward::getChestId)
                .collect(Collectors.toUnmodifiableSet());

        // tạo 1 map gồm:
        // key là chest id
        // value là chest result
        Map<Long, ChestResult> chestResults =
                chestRepositoryPort.findAllByIdIn(chestIds)
                        .stream()
                        .collect(Collectors.toUnmodifiableMap(
                                Chest::getId,
                                chestResultMapper::domainToResult
                        ));

        return dailyRewards.stream()
                .map(dailyReward ->
                        dailyRewardResultMapper.domainToResult(
                                dailyReward,
                                chestResults.get(dailyReward.getChestId())
                        )
                )
                .toList();
    }

}
