package org.naho.daily.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.naho.daily.entity.DailyMissionEntity;
import org.naho.daily.entity.UserDailyMissionEntity;
import org.naho.daily.model.UserDailyMission;
import org.naho.daily.repository.DailyMissionJpaRepository;
import org.naho.user.entity.UserEntity;
import org.naho.user.repository.UserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class UserDailyMissionEntityMapper {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private DailyMissionJpaRepository dailyMissionJpaRepository;

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "dailyMissionId", source = "dailyMission.id")
    public abstract UserDailyMission entityToDomain(UserDailyMissionEntity entity);


    @Mapping(
            target = "user",
            source = "userId",
            qualifiedByName = "getUserEntityReferenceById"
    )
    @Mapping(
            target = "dailyMission",
            source = "dailyMissionId",
            qualifiedByName = "getDailyMissionEntityReferenceById"
    )
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    public abstract UserDailyMissionEntity domainToEntity(UserDailyMission domain);

    @Named("getUserEntityReferenceById")
    protected UserEntity getUserEntityReferenceById(Long userId) {
        return userJpaRepository.getReferenceById(userId);
    }

    @Named("getDailyMissionEntityReferenceById")
    protected DailyMissionEntity getDailyMissionEntityReferenceById(Long dailyMissionId) {
        return dailyMissionJpaRepository.getReferenceById(dailyMissionId);
    }
}
