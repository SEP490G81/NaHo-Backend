package org.naho.daily.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.naho.daily.entity.DailyRewardEntity;
import org.naho.daily.model.DailyReward;
import org.naho.daily.valueobject.RewardYearMonth;

@Mapper(componentModel = "spring")
public interface DailyEntityMapper {
    @Mapping(target = "chestId", source = "chest.id")
    @Mapping(
            target = "rewardYearMonth",
            source = "rewardYearMonth",
            qualifiedByName = "stringToRewardYearMonth"
    )
    DailyReward entityToDomain(DailyRewardEntity entity);

    @Mapping(target = "chest", ignore = true)
    @Mapping(
            target = "rewardYearMonth",
            source = "rewardYearMonth.value"
    )
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    @Mapping(target = "userDailyAttendances", ignore = true)
    DailyRewardEntity domainToEntity(DailyReward domain);

    @Named("stringToRewardYearMonth")
    default RewardYearMonth stringToRewardYearMonth(String value) {
        return RewardYearMonth.of(value);
    }
}
