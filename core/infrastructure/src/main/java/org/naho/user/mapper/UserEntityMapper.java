package org.naho.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.daily.mapper.UserDailyAttendanceIdMapper;
import org.naho.daily.mapper.UserDailyMissionIdMapper;
import org.naho.file.mapper.FileIdMapper;
import org.naho.learning.mapper.UserLearningProgressIdMapper;
import org.naho.learning.mapper.UserNodeProgressIdMapper;
import org.naho.point.mapper.PointHistoryIdMapper;
import org.naho.question.mapper.SpeakingQuestionIdMapper;
import org.naho.social.report.mapper.ReportIdMapper;
import org.naho.user.entity.UserEntity;
import org.naho.user.model.User;

@Mapper(componentModel = "spring", uses = {
        UserValueObjectMapper.class,
        RoleIdMapper.class,
        UserSessionIdMapper.class,
        OAuthProviderIdMapper.class,
        SpeakingQuestionIdMapper.class,
        ReportIdMapper.class,
        PointHistoryIdMapper.class,
        UserNodeProgressIdMapper.class,
        UserDailyAttendanceIdMapper.class,
        UserDailyMissionIdMapper.class,
        UserLearningProgressIdMapper.class,
        FileIdMapper.class
})
public interface UserEntityMapper {
    @Mapping(target = "roleIds", source = "roles")
    @Mapping(target = "userSessionIds", source = "userSessions")
    @Mapping(target = "oAuthProviderIds", source = "OAuthProviders")
    @Mapping(target = "speakingQuestionIds", source = "speakingQuestions")
    @Mapping(target = "reportIds", source = "reports")
    @Mapping(target = "pointHistoryIds", source = "pointHistories")
    @Mapping(target = "userNodeProgressIds", source = "userNodeProgresses")
    @Mapping(target = "userDailyAttendanceIds", source = "userDailyAttendances")
    @Mapping(target = "userDailyMissionIds", source = "userDailyMissions")
    @Mapping(target = "avatarFileId", source = "avatar.id")
    @Mapping(target = "userLearningProgressId", source = "userLearningProgress.id")
    @Mapping(target = "isEmailVerified", source = "emailVerified")
    User entityToDomain(UserEntity entity);

    @Mapping(target = "roles", source = "roleIds")
    @Mapping(target = "jlptLevel", source = "jlptLevel")
    @Mapping(target = "userLearningProgress", source = "userLearningProgressId")
    @Mapping(target = "userSessions", source = "userSessionIds")
    @Mapping(target = "speakingQuestions", source = "speakingQuestionIds")
    @Mapping(target = "reports", source = "reportIds")
    @Mapping(target = "pointHistories", source = "pointHistoryIds")
    @Mapping(target = "userNodeProgresses", source = "userNodeProgressIds")
    @Mapping(target = "oAuthProviders", source = "OAuthProviderIds")
    @Mapping(target = "avatar", source = "avatarFileId")
    @Mapping(target = "userDailyAttendances", source = "userDailyAttendanceIds")
    @Mapping(target = "userDailyMissions", source = "userDailyMissionIds")
    @Mapping(target = "isEmailVerified", source = "emailVerified")
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    UserEntity domainToEntity(User user);
}
