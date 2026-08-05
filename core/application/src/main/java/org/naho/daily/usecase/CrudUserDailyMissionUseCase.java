package org.naho.daily.usecase;

import org.naho.daily.command.CompleteDailyMissionCommand;
import org.naho.daily.command.EarnDailyMissionCommand;
import org.naho.daily.exception.DailyMissionDomainErrorCode;
import org.naho.daily.exception.UserDailyMissionDomainErrorCode;
import org.naho.daily.mapper.UserDailyMissionResultMapper;
import org.naho.daily.model.DailyMission;
import org.naho.daily.model.UserDailyMission;
import org.naho.daily.port.in.CrudUserDailyMissionInputPort;
import org.naho.daily.port.out.DailyMissionRepositoryPort;
import org.naho.daily.port.out.UserDailyMissionRepositoryPort;
import org.naho.daily.result.UserDailyMissionResult;
import org.naho.daily.type.MissionStatus;
import org.naho.i18n.message.daily.DailyMissionDetailMessageKey;
import org.naho.i18n.message.daily.UserDailyMissionDetailMessageKey;
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

import java.time.LocalDate;
import java.util.List;

public class CrudUserDailyMissionUseCase implements CrudUserDailyMissionInputPort {
    public static final int NUMBER_OF_MISSIONS_PER_DAY = 2;
    public static final long TALK_WITH_AI_MISSION_ID = 1L;
    public static final long COMPLETE_SPEAKING_QUESTION_NODE_MISSION_ID = 2L;

    private final UserDailyMissionRepositoryPort userDailyMissionRepositoryPort;
    private final UserDailyMissionResultMapper userDailyMissionResultMapper;
    private final UserLearningProgressRepositoryPort userLearningProgressRepositoryPort;
    private final DailyMissionRepositoryPort dailyMissionRepositoryPort;
    private final CrudPointHistoryInputPort crudPointHistoryInputPort;
    private final TransactionPort transactionPort;

    public CrudUserDailyMissionUseCase(
            UserDailyMissionRepositoryPort userDailyMissionRepositoryPort,
            UserDailyMissionResultMapper userDailyMissionResultMapper,
            UserLearningProgressRepositoryPort userLearningProgressRepositoryPort,
            DailyMissionRepositoryPort dailyMissionRepositoryPort,
            CrudPointHistoryInputPort crudPointHistoryInputPort,
            TransactionPort transactionPort) {
        this.userDailyMissionRepositoryPort = userDailyMissionRepositoryPort;
        this.userDailyMissionResultMapper = userDailyMissionResultMapper;
        this.userLearningProgressRepositoryPort = userLearningProgressRepositoryPort;
        this.dailyMissionRepositoryPort = dailyMissionRepositoryPort;
        this.crudPointHistoryInputPort = crudPointHistoryInputPort;
        this.transactionPort = transactionPort;
    }

    @Override
    public List<UserDailyMissionResult> findAllUserTodayMissions(Long userId) {
        return transactionPort.execute(() -> doFindAllUserTodayMissions(userId));
    }

    private List<UserDailyMissionResult> doFindAllUserTodayMissions(Long userId) {
        LocalDate today = LocalDate.now(SystemZoneId.HO_CHI_MINH_ZONE_ID);

        List<UserDailyMission> currentUserDailyMissions = userDailyMissionRepositoryPort
                .findAllByUser_IdAndStartedDate(userId, today);

        // nếu người dùng chưa tạo nhiệm vụ của ngày hôm nay
        if (currentUserDailyMissions.isEmpty()) {
            UserDailyMission talkWithAiMission = UserDailyMission.builder()
                    .userId(userId)
                    .dailyMissionId(TALK_WITH_AI_MISSION_ID)
                    .status(MissionStatus.IN_PROGRESS)
                    .startedDate(today)
                    .build();
            UserDailyMission savedTalkWithAiMission = userDailyMissionRepositoryPort.save(talkWithAiMission);

            UserDailyMission completeSpeakingQuestionNodeMission = UserDailyMission.builder()
                    .userId(userId)
                    .dailyMissionId(COMPLETE_SPEAKING_QUESTION_NODE_MISSION_ID)
                    .status(MissionStatus.IN_PROGRESS)
                    .startedDate(today)
                    .build();
            UserDailyMission savedCompleteSpeakingQuestionNodeMission = userDailyMissionRepositoryPort.save(completeSpeakingQuestionNodeMission);

            currentUserDailyMissions = List.of(savedTalkWithAiMission, savedCompleteSpeakingQuestionNodeMission);
        }

        // nếu size không hợp lệ, ném ra lỗi
        if (currentUserDailyMissions.size() != NUMBER_OF_MISSIONS_PER_DAY) {
            throw new ApplicationException(
                    UserDailyMissionDomainErrorCode.USER_DAILY_MISSION_NOT_FOUND,
                    UserDailyMissionDetailMessageKey.USER_DAILY_MISSION_NOT_FOUND,
                    userId
            );
        }

        // nếu đã có thì trả về
        return currentUserDailyMissions
                .stream().map(userDailyMissionResultMapper::domainToResult)
                .toList();
    }

    @Override
    public UserDailyMissionResult completeMission(CompleteDailyMissionCommand command) {
        LocalDate today = LocalDate.now(SystemZoneId.HO_CHI_MINH_ZONE_ID);

        // init nếu chưa tồn tại missions
        findAllUserTodayMissions(command.userId());

        // tìm user daily mission bằng user id, loại mission và thời gian start
        UserDailyMission currentUserDailyMission = userDailyMissionRepositoryPort
                .findByUserIdAndDailyMissionMissionTypeAndStartedDate(
                        command.userId(),
                        command.missionType(),
                        today
                )
                .orElseThrow(() -> new ApplicationException(
                        UserDailyMissionDomainErrorCode.USER_DAILY_MISSION_NOT_FOUND,
                        UserDailyMissionDetailMessageKey.USER_DAILY_MISSION_NOT_FOUND,
                        command.userId()
                ));

        // nếu đã hoàn thành hoặc đã nhận thưởng rồi thì thôi
        if (currentUserDailyMission.getStatus() != MissionStatus.IN_PROGRESS) {
            return userDailyMissionResultMapper.domainToResult(currentUserDailyMission);
        }

        currentUserDailyMission.setStatus(MissionStatus.COMPLETED);
        currentUserDailyMission.setCompletedDate(today);
        UserDailyMission savedUserDailyMission = userDailyMissionRepositoryPort.save(currentUserDailyMission);
        return userDailyMissionResultMapper.domainToResult(savedUserDailyMission);
    }

    @Override
    public UserDailyMissionResult earnMission(EarnDailyMissionCommand command) {
        LocalDate today = LocalDate.now(SystemZoneId.HO_CHI_MINH_ZONE_ID);

        // tìm user daily mission theo id khóa chính và user id
        UserDailyMission currentUserDailyMission = userDailyMissionRepositoryPort
                .findByIdAndUserId(
                        command.userDailyMissionId(),
                        command.userId()
                )
                .orElseThrow(() -> new ApplicationException(
                        UserDailyMissionDomainErrorCode.USER_DAILY_MISSION_NOT_FOUND,
                        UserDailyMissionDetailMessageKey.USER_DAILY_MISSION_NOT_FOUND,
                        command.userDailyMissionId()
                ));

        // phải hoàn thành nhiệm vụ thì mới nhận thưởng được
        if (currentUserDailyMission.getStatus() == MissionStatus.IN_PROGRESS) {
            throw new ApplicationException(
                    UserDailyMissionDomainErrorCode.USER_DAILY_MISSION_NOT_COMPLETED,
                    UserDailyMissionDetailMessageKey.USER_DAILY_MISSION_NOT_COMPLETED,
                    currentUserDailyMission.getId()
            );
        }

        // nhận thưởng rồi thì thôi
        if (currentUserDailyMission.getStatus() == MissionStatus.EARNED) {
            throw new ApplicationException(
                    UserDailyMissionDomainErrorCode.USER_DAILY_MISSION_ALREADY_EARNED,
                    UserDailyMissionDetailMessageKey.USER_DAILY_MISSION_ALREADY_EARNED,
                    currentUserDailyMission.getId()
            );
        }

        currentUserDailyMission.setStatus(MissionStatus.EARNED);
        currentUserDailyMission.setEarnedDate(today);

        UserDailyMission savedUserDailyMission = userDailyMissionRepositoryPort.save(currentUserDailyMission);

        DailyMission dailyMission = dailyMissionRepositoryPort
                .findById(currentUserDailyMission.getDailyMissionId())
                .orElseThrow(() -> new ApplicationException(
                        DailyMissionDomainErrorCode.DAILY_MISSION_NOT_FOUND,
                        DailyMissionDetailMessageKey.DAILY_MISSION_NOT_FOUND,
                        currentUserDailyMission.getDailyMissionId()
                ));

        // cộng total point vào user learning progress
        UserLearningProgress progress = userLearningProgressRepositoryPort
                .findByUserId(currentUserDailyMission.getUserId())
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

        return userDailyMissionResultMapper.domainToResult(savedUserDailyMission);
    }
}
