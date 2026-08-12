package org.naho.subscription.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.subscription.entity.UserDailyAiUsageEntity;
import org.naho.subscription.model.UserDailyAiUsage;
import org.naho.user.mapper.UserIdMapper;

@Mapper(componentModel = "spring", uses = {UserIdMapper.class})
public interface UserDailyAiUsageEntityMapper {
    @Mapping(target = "userId", source = "user.id")
    UserDailyAiUsage entityToDomain(UserDailyAiUsageEntity entity);

    @Mapping(target = "user", source = "userId")
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    UserDailyAiUsageEntity domainToEntity(UserDailyAiUsage domain);
}
