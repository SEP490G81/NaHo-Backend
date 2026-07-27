package org.naho.daily.usecase;

import org.naho.daily.command.CompleteMissionCommand;
import org.naho.daily.exception.DailyMissionDomainErrorCode;
import org.naho.daily.mapper.DailyMissionResultMapper;
import org.naho.daily.model.DailyMission;
import org.naho.daily.model.UserDailyMission;
import org.naho.daily.port.in.CrudDailyMissionInputPort;
import org.naho.daily.port.out.DailyMissionRepositoryPort;
import org.naho.daily.port.out.UserDailyMissionRepositoryPort;
import org.naho.daily.result.DailyMissionResult;
import org.naho.daily.type.MissionType;
import org.naho.i18n.message.daily.DailyMissionDetailMessageKey;
import org.naho.i18n.message.learning.UserLearningProgressDetailMessageKey;
import org.naho.learning.exception.UserLearningProgressErrorCode;
import org.naho.learning.model.UserLearningProgress;
import org.naho.learning.port.out.UserLearningProgressRepositoryPort;
import org.naho.point.command.PointHistoryCommand;
import org.naho.point.port.in.CrudPointHistoryInputPort;
import org.naho.point.type.PointTransactionType;
import org.naho.shared.constant.SystemZoneId;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public class CrudDailyMissionUseCase implements CrudDailyMissionInputPort {
    private static final Double DEFAULT_POINT = 5.0;
    private final DailyMissionRepositoryPort dailyMissionRepositoryPort;
    private final UserDailyMissionRepositoryPort userDailyMissionRepositoryPort;
    private final UserLearningProgressRepositoryPort userLearningProgressRepositoryPort;
    private final CrudPointHistoryInputPort crudPointHistoryInputPort;
    private final DailyMissionResultMapper dailyMissionResultMapper;
    private final TransactionPort transactionPort;

    public CrudDailyMissionUseCase(
            DailyMissionRepositoryPort dailyMissionRepositoryPort,
            UserDailyMissionRepositoryPort userDailyMissionRepositoryPort,
            UserLearningProgressRepositoryPort userLearningProgressRepositoryPort,
            CrudPointHistoryInputPort crudPointHistoryInputPort,
            DailyMissionResultMapper dailyMissionResultMapper,
            TransactionPort transactionPort
    ) {
        this.dailyMissionRepositoryPort = dailyMissionRepositoryPort;
        this.userDailyMissionRepositoryPort = userDailyMissionRepositoryPort;
        this.userLearningProgressRepositoryPort = userLearningProgressRepositoryPort;
        this.crudPointHistoryInputPort = crudPointHistoryInputPort;
        this.dailyMissionResultMapper = dailyMissionResultMapper;
        this.transactionPort = transactionPort;
    }

    @Override
    public void createTodayMissions() {
        LocalDate today = LocalDate.now(SystemZoneId.HO_CHI_MINH_ZONE_ID);
        if (!dailyMissionRepositoryPort.existsByMissionDate(today)) {
            DailyMission talkWithAi = DailyMission.builder()
                    .missionDate(today)
                    .missionType(MissionType.TALK_WITH_AI)
                    .point(DEFAULT_POINT)
                    .build();

            dailyMissionRepositoryPort.save(talkWithAi);

            DailyMission completeANode = DailyMission.builder()
                    .missionDate(today)
                    .missionType(MissionType.COMPLETE_SPEAKING_QUESTION_NODE)
                    .point(DEFAULT_POINT)
                    .build();

            dailyMissionRepositoryPort.save(completeANode);
        }
    }

    @Override
    public void completeMission(CompleteMissionCommand command) {
        transactionPort.execute(() -> doCompleteMission(command));
    }

    private void doCompleteMission(CompleteMissionCommand command) {
        LocalDate today = LocalDate.now(SystemZoneId.HO_CHI_MINH_ZONE_ID);

        DailyMission dailyMission = dailyMissionRepositoryPort
                .findByMissionDateAndMissionType(today, command.missionType())
                .orElseThrow(() -> new ApplicationException(
                        DailyMissionDomainErrorCode.DAILY_MISSION_NOT_FOUND,
                        DailyMissionDetailMessageKey.DAILY_MISSION_NOT_FOUND
                ));

        // nếu đã hoàn thành nhiệm vụ này rồi
        if (userDailyMissionRepositoryPort.existsByUserIdAndDailyMissionId(
                command.userId(),
                dailyMission.getId()
        )) {
            return;
        }

        UserDailyMission userDailyMission = UserDailyMission.builder()
                .userId(command.userId())
                .dailyMissionId(dailyMission.getId())
                .completedAt(Instant.now())
                .build();

        userDailyMissionRepositoryPort.save(userDailyMission);

        // cộng total point vào user learning progress
        UserLearningProgress progress = userLearningProgressRepositoryPort
                .findByUserId(command.userId())
                .orElseThrow(() -> new ApplicationException(
                        UserLearningProgressErrorCode.USER_LEARNING_PROGRESS_NOT_FOUND,
                        UserLearningProgressDetailMessageKey.USER_LEARNING_PROGRESS_NOT_FOUND_BY_USER_ID,
                        command.userId()
                ));

        progress.addPoint(dailyMission.getPoint());

        userLearningProgressRepositoryPort.save(progress);

        // lưu lịch sử nhận điểm
        crudPointHistoryInputPort.createPointHistory(
                PointHistoryCommand.builder()
                        .userId(command.userId())
                        .point(dailyMission.getPoint())
                        .transactionType(PointTransactionType.DAILY_MISSION_REWARD)
                        .build()
        );
    }

    @Override
    public List<DailyMissionResult> getTodayMissions() {
        LocalDate today = LocalDate.now(SystemZoneId.HO_CHI_MINH_ZONE_ID);
        return dailyMissionRepositoryPort
                .findAllByMissionDate(today)
                .stream().map(dailyMissionResultMapper::domainToResult)
                .toList();
    }
}

