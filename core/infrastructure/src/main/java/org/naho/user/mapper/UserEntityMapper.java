package org.naho.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.user.entity.UserEntity;
import org.naho.user.model.User;

@Mapper(
        componentModel = "spring",
        uses = {
                RoleEntityMapper.class,
                UserValueObjectMapper.class
        }
)
public interface UserEntityMapper {
    @Mapping(target = "roleIds", source = "roles")
    @Mapping(target = "pointSummaryId", source = "pointSummary.id")
    @Mapping(target = "userSessionIds", ignore = true)
    User entityToDomain(UserEntity entity);

    @Mapping(target = "roles", source = "roleIds")
    @Mapping(target = "jlptLevel", defaultValue = "N5")
    @Mapping(target = "userSessions", ignore = true)
    @Mapping(target = "questions", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    @Mapping(target = "reports", ignore = true)
    @Mapping(target = "pointHistories", ignore = true)
    @Mapping(target = "pointSummary", ignore = true)
    @Mapping(target = "userNodeProgresses", ignore = true)
    @Mapping(target = "userLearningProgress", ignore = true)
    @Mapping(target = "userSeasonPoints", ignore = true)
    UserEntity domainToEntity(User user);
}
