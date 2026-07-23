package org.naho.daily.usecase;

import org.naho.chest.exception.ChestErrorCode;
import org.naho.chest.model.Chest;
import org.naho.chest.port.out.ChestRepositoryPort;
import org.naho.daily.command.EarnDailyRewardCommand;
import org.naho.daily.exception.DailyRewardErrorCode;
import org.naho.daily.mapper.DailyRewardResultMapper;
import org.naho.daily.mapper.UserDailyAttendanceResultMapper;
import org.naho.daily.model.DailyReward;
import org.naho.daily.model.UserDailyAttendance;
import org.naho.daily.port.in.CrudDailyRewardInputPort;
import org.naho.daily.port.out.DailyRewardRepositoryPort;
import org.naho.daily.port.out.UserDailyAttendanceRepositoryPort;
import org.naho.daily.result.DailyRewardResult;
import org.naho.daily.result.UserDailyAttendanceResult;
import org.naho.daily.valueobject.RewardYearMonth;
import org.naho.i18n.message.chest.ChestDetailMessageKey;
import org.naho.i18n.message.daily.DailyRewardDetailMessageKey;
import org.naho.i18n.message.daily.UserDailyAttendanceDetailMessageKey;
import org.naho.i18n.message.learning.UserLearningProgressDetailMessageKey;
import org.naho.learning.exception.UserLearningProgressErrorCode;
import org.naho.learning.model.UserLearningProgress;
import org.naho.learning.port.in.CrudUserLearningProgressInputPort;
import org.naho.learning.port.out.UserLearningProgressRepositoryPort;
import org.naho.point.command.PointHistoryCommand;
import org.naho.point.port.in.CrudPointHistoryInputPort;
import org.naho.point.type.PointTransactionType;
import org.naho.shared.constant.SystemZoneId;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class CrudDailyRewardUseCase implements CrudDailyRewardInputPort {

    private final DailyRewardRepositoryPort dailyRewardRepositoryPort;
    private final DailyRewardResultMapper dailyRewardResultMapper;
    private final ChestRepositoryPort chestRepositoryPort;
    private final UserLearningProgressRepositoryPort userLearningProgressRepositoryPort;
    private final UserDailyAttendanceRepositoryPort userDailyAttendanceRepositoryPort;
    private final UserDailyAttendanceResultMapper userDailyAttendanceResultMapper;
    private final CrudPointHistoryInputPort crudPointHistoryInputPort;
    private final CrudUserLearningProgressInputPort crudUserLearningProgressInputPort;
    private final TransactionPort transactionPort;

    public CrudDailyRewardUseCase(
            DailyRewardRepositoryPort dailyRewardRepositoryPort,
            DailyRewardResultMapper dailyRewardResultMapper,
            ChestRepositoryPort chestRepositoryPort,
            UserLearningProgressRepositoryPort userLearningProgressRepositoryPort,
            UserDailyAttendanceRepositoryPort userDailyAttendanceRepositoryPort,
            UserDailyAttendanceResultMapper userDailyAttendanceResultMapper,
            CrudPointHistoryInputPort crudPointHistoryInputPort,
            CrudUserLearningProgressInputPort crudUserLearningProgressInputPort,
            TransactionPort transactionPort
    ) {
        this.dailyRewardRepositoryPort = dailyRewardRepositoryPort;
        this.dailyRewardResultMapper = dailyRewardResultMapper;
        this.chestRepositoryPort = chestRepositoryPort;
        this.userLearningProgressRepositoryPort = userLearningProgressRepositoryPort;
        this.userDailyAttendanceRepositoryPort = userDailyAttendanceRepositoryPort;
        this.userDailyAttendanceResultMapper = userDailyAttendanceResultMapper;
        this.crudPointHistoryInputPort = crudPointHistoryInputPort;
        this.crudUserLearningProgressInputPort = crudUserLearningProgressInputPort;
        this.transactionPort = transactionPort;
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

        // nếu tháng này đã tồn tại thì ném ra lỗi
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
            } else if (localDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)) { // nếu là Chủ Nhật
                dailyReward.setChestId(2L);
            } else { // các ngày còn lại
                dailyReward.setChestId(1L);
            }

            dailyRewards.add(dailyReward);
        }

        List<DailyReward> savedDailyRewards = dailyRewardRepositoryPort.saveAll(dailyRewards);
        return dailyRewardResultMapper.domainListToResultList(savedDailyRewards);
    }

    @Override
    public UserDailyAttendanceResult earnDailyReward(EarnDailyRewardCommand command) {
        return transactionPort.execute(() -> doEarnDailyReward(command));
    }

    public UserDailyAttendanceResult doEarnDailyReward(EarnDailyRewardCommand command) {
        LocalDate now = LocalDate.now(SystemZoneId.HO_CHI_MINH_ZONE_ID);

        // nếu điểm danh 1 phần quà không phải của hôm nay thì ném lỗi
        DailyReward dailyReward = dailyRewardRepositoryPort
                .findById(command.dailyRewardId())
                .orElseThrow(() -> new ApplicationException(
                        DailyRewardErrorCode.DAILY_REWARD_NOT_FOUND,
                        DailyRewardDetailMessageKey.DAILY_REWARD_NOT_FOUND,
                        command.dailyRewardId()
                ));

        if (!dailyReward.getLocalDate().equals(now)) {
            throw new ApplicationException(
                    DailyRewardErrorCode.DAILY_REWARD_NOT_FOR_TODAY,
                    DailyRewardDetailMessageKey.DAILY_REWARD_NOT_FOR_TODAY
            );
        }

        // nếu đã điểm danh ngày này rồi thì ném lỗi
        if (userDailyAttendanceRepositoryPort.existsByUser_IdAndAttendanceDate(command.userId(), now)) {
            throw new ApplicationException(
                    DailyRewardErrorCode.USER_DAILY_ATTENDANCE_ALREADY_EXISTS,
                    UserDailyAttendanceDetailMessageKey.USER_DAILY_ATTENDANCE_ALREADY_EXISTS
            );
        }

        // update user learning progress: add point
        UserLearningProgress userLearningProgress = userLearningProgressRepositoryPort
                .findByUserId(command.userId())
                .orElseThrow(() -> new ApplicationException(
                        UserLearningProgressErrorCode.USER_LEARNING_PROGRESS_NOT_FOUND,
                        UserLearningProgressDetailMessageKey.USER_LEARNING_PROGRESS_NOT_FOUND_BY_USER_ID,
                        command.userId()
                ));

        Chest chest = chestRepositoryPort
                .findByDailyRewardId(command.dailyRewardId())
                .orElseThrow(() -> new ApplicationException(
                        ChestErrorCode.CHEST_NOT_FOUND,
                        ChestDetailMessageKey.CHEST_NOT_FOUND,
                        command.dailyRewardId()
                ));

        double earnedPoint = chest.getRandomPoint();
        userLearningProgress.addPoint(earnedPoint);

        userLearningProgressRepositoryPort.save(userLearningProgress);

        // save the earned point history
        crudPointHistoryInputPort.createPointHistory(PointHistoryCommand.builder()
                .userId(command.userId())
                .point(earnedPoint)
                .transactionType(PointTransactionType.DAILY_REWARD)
                .build());

        // save the daily reward attendance
        UserDailyAttendance userDailyAttendance = UserDailyAttendance.builder()
                .userId(command.userId())
                .dailyRewardId(command.dailyRewardId())
                .attendanceDate(now)
                .build();

        UserDailyAttendance savedUserDailyAttendance =
                userDailyAttendanceRepositoryPort.save(userDailyAttendance);

        return userDailyAttendanceResultMapper.domainToResult(savedUserDailyAttendance);
    }
}
