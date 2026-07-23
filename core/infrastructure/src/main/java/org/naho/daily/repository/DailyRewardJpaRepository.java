package org.naho.daily.repository;

import org.naho.daily.entity.DailyRewardEntity;
import org.naho.shared.persistence.BaseJpaRepository;

import java.util.List;

public interface DailyRewardJpaRepository extends BaseJpaRepository<DailyRewardEntity> {
    List<DailyRewardEntity> findAllByRewardYearMonthOrderByDayOfMonth(String rewardYearMonth);

    boolean existsByRewardYearMonth(String rewardYearMonth);
}
