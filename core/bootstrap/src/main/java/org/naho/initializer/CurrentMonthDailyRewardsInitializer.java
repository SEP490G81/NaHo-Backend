package org.naho.initializer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.daily.port.in.CrudDailyRewardInputPort;
import org.naho.daily.port.out.DailyRewardRepositoryPort;
import org.naho.daily.valueobject.RewardYearMonth;
import org.naho.shared.constant.SystemZoneId;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.YearMonth;

@Slf4j
@Order(5)
@Component
@RequiredArgsConstructor
public class CurrentMonthDailyRewardsInitializer implements ApplicationRunner {
    private final CrudDailyRewardInputPort crudDailyRewardInputPort;
    private final DailyRewardRepositoryPort dailyRewardRepositoryPort;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        YearMonth yearMonth = YearMonth.now(SystemZoneId.HO_CHI_MINH_ZONE_ID);
        RewardYearMonth rewardYearMonth = RewardYearMonth.of(yearMonth);

        if (dailyRewardRepositoryPort.existsByRewardYearMonth(rewardYearMonth.getValue())) {
            log.info("Current Month Daily Rewards existed!");
        } else {
            log.info("Initializing Current Month Daily Rewards...");
            crudDailyRewardInputPort.createMonthlyDailyRewards(yearMonth);
            log.info("Current Month Daily Rewards initialized!");
        }
    }
}
