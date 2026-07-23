package org.naho.daily.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.daily.entity.DailyRewardEntity;
import org.naho.daily.mapper.DailyEntityMapper;
import org.naho.daily.model.DailyReward;
import org.naho.daily.port.out.DailyRewardRepositoryPort;
import org.naho.daily.repository.DailyRewardJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DailyRewardRepositoryAdapter implements DailyRewardRepositoryPort {

    private final DailyRewardJpaRepository dailyRewardJpaRepository;
    private final DailyEntityMapper dailyEntityMapper;

    @Override
    public List<DailyReward> findAllByRewardYearMonthOrderByDayOfMonth(String rewardYearMonth) {
        List<DailyRewardEntity> dailyRewardEntityList =
                dailyRewardJpaRepository.findAllByRewardYearMonthOrderByDayOfMonth(rewardYearMonth);

        return dailyRewardEntityList.stream()
                .map(dailyEntityMapper::entityToDomain)
                .toList();
    }

    @Override
    public boolean existsByRewardYearMonth(String rewardYearMonth) {
        return dailyRewardJpaRepository.existsByRewardYearMonth(rewardYearMonth);
    }

    @Override
    public List<DailyReward> saveAll(List<DailyReward> dailyRewards) {
        List<DailyRewardEntity> dailyRewardEntityList = dailyRewards.stream()
                .map(dailyEntityMapper::domainToEntity)
                .toList();

        List<DailyRewardEntity> savedDailyRewardEntityList =
                dailyRewardJpaRepository.saveAll(dailyRewardEntityList);

        return savedDailyRewardEntityList.stream()
                .map(dailyEntityMapper::entityToDomain)
                .toList();
    }
}
