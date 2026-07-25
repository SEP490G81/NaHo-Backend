package org.naho.daily.mapper;

import org.naho.chest.mapper.ChestResultMapper;
import org.naho.chest.model.Chest;
import org.naho.chest.port.out.ChestRepositoryPort;
import org.naho.chest.result.ChestResult;
import org.naho.daily.model.DailyReward;
import org.naho.daily.result.DailyRewardResult;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DailyRewardResultMapper {

    private final ChestRepositoryPort chestRepositoryPort;
    private final ChestResultMapper chestResultMapper;

    public DailyRewardResultMapper(
            ChestRepositoryPort chestRepositoryPort,
            ChestResultMapper chestResultMapper
    ) {
        this.chestRepositoryPort = chestRepositoryPort;
        this.chestResultMapper = chestResultMapper;
    }

    public DailyRewardResult domainToResult(DailyReward domain, ChestResult chestResult) {
        return DailyRewardResult.builder()
                .id(domain.getId())
                .chest(chestResult)
                .rewardYearMonth(domain.getRewardYearMonth().getValue())
                .dayOfMonth(domain.getDayOfMonth())
                .build();
    }

    public List<DailyRewardResult> domainListToResultList(List<DailyReward> domainList) {
        // lấy danh sách các chestIds từ domainList
        Set<Long> chestIds = domainList.stream()
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

        return domainList.stream()
                .map(dailyReward ->
                        domainToResult(dailyReward, chestResults.get(dailyReward.getChestId()))
                )
                .toList();
    }
}
