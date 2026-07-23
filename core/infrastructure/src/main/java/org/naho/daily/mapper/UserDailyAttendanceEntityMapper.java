package org.naho.daily.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.naho.daily.entity.DailyRewardEntity;
import org.naho.daily.entity.UserDailyAttendanceEntity;
import org.naho.daily.model.UserDailyAttendance;
import org.naho.daily.repository.DailyRewardJpaRepository;
import org.naho.user.entity.UserEntity;
import org.naho.user.repository.UserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class UserDailyAttendanceEntityMapper {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private DailyRewardJpaRepository dailyRewardJpaRepository;

    @Mapping(
            target = "user",
            source = "userId",
            qualifiedByName = "getUserEntityReferenceById"
    )
    @Mapping(
            target = "dailyReward",
            source = "dailyRewardId",
            qualifiedByName = "getDailyRewardEntityReferenceById"
    )
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    public abstract UserDailyAttendanceEntity domainToEntity(UserDailyAttendance domain);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "dailyRewardId", source = "dailyReward.id")
    public abstract UserDailyAttendance entityToDomain(UserDailyAttendanceEntity entity);

    @Named("getUserEntityReferenceById")
    protected UserEntity getUserEntityReferenceById(Long userId) {
        return userJpaRepository.getReferenceById(userId);
    }

    @Named("getDailyRewardEntityReferenceById")
    protected DailyRewardEntity getDailyRewardEntityReferenceById(Long dailyRewardId) {
        return dailyRewardJpaRepository.getReferenceById(dailyRewardId);
    }
}
