package org.naho.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.daily.port.in.CrudDailyRewardInputPort;
import org.naho.shared.constant.SystemZoneId;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;

@Component
@RequiredArgsConstructor
@Slf4j
public class DailyRewardScheduler {
    private final CrudDailyRewardInputPort crudDailyRewardInputPort;

    /*
     * nếu không phải ngày cuối thàng thì không chạy
     * ┌──────── second (0 - 59)
     * │ ┌────── minute (0 - 59)
     * │ │ ┌──── hour (0 - 23)
     * │ │ │ ┌── day of month (1 - 31)
     * │ │ │ │ ┌ month (1 - 12)
     * │ │ │ │ │ ┌ day of week (0 - 7)
     * │ │ │ │ │ │
     * * * * * * *
     * '0 0 1 * * *' nghĩa là: giây thứ 0, phút thứ 0, 1 giờ sáng,
     * mọi ngày trong tháng, mọi tháng, mọi thứ trong tuần
     */
    @Scheduled(cron = "0 0 1 * * *", zone = SystemZoneId.HO_CHI_MINH_ZONE_ID_NAME)
    public void createNextMonthDailyRewards() {
        LocalDate today = LocalDate.now(SystemZoneId.HO_CHI_MINH_ZONE_ID);

        if (!today.equals(today.with(TemporalAdjusters.lastDayOfMonth()))) {
            return;
        }

        YearMonth nextYearMonth = YearMonth.from(today).plusMonths(1);

        log.info("Initializing Daily Rewards for next month...");
        crudDailyRewardInputPort.createMonthlyDailyRewards(nextYearMonth);
        log.info("Daily Rewards for next month initialized!");
    }
}
