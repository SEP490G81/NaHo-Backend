package org.naho.daily.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.naho.chest.entity.ChestEntity;
import org.naho.chest.repository.ChestJpaRepository;
import org.naho.daily.entity.DailyRewardEntity;
import org.naho.daily.model.DailyReward;
import org.naho.daily.valueobject.RewardYearMonth;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class DailyEntityMapper {
    @Autowired
    private ChestJpaRepository chestJpaRepository;

    @Mapping(target = "chestId", source = "chest.id")
    @Mapping(
            target = "rewardYearMonth",
            source = "rewardYearMonth",
            qualifiedByName = "stringToRewardYearMonth"
    )
    public abstract DailyReward entityToDomain(DailyRewardEntity entity);

    @Mapping(
            target = "chest",
            source = "chestId",
            qualifiedByName = "getChestReferenceById"
    )
    @Mapping(
            target = "rewardYearMonth",
            source = "rewardYearMonth.value"
    )
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    @Mapping(target = "userDailyAttendances", ignore = true)
    public abstract DailyRewardEntity domainToEntity(DailyReward domain);

    @Named("getChestReferenceById")
    protected ChestEntity getChestReferenceById(Long chestId) {
        return chestJpaRepository.getReferenceById(chestId);
    }

    @Named("stringToRewardYearMonth")
    protected RewardYearMonth stringToRewardYearMonth(String value) {
        return RewardYearMonth.of(value);
    }
}
