package org.naho.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.naho.user.entity.OAuthProviderEntity;
import org.naho.user.entity.UserEntity;
import org.naho.user.model.User;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {
                RoleEntityMapper.class,
                UserValueObjectMapper.class
        }
)
public interface UserEntityMapper {
    @Mapping(target = "roleIds", source = "roles")
    @Mapping(target = "userLearningProgressId", source = "userLearningProgress.id")
    @Mapping(target = "userSessionIds", ignore = true)
    @Mapping(target = "oAuthProviderIds", source = "OAuthProviders", qualifiedByName = "getOAuthProviderIds")
    @Mapping(target = "avatarFileId", source = "avatar.id")
    @Mapping(target = "isEmailVerified", source = "emailVerified")
    User entityToDomain(UserEntity entity);

    @Mapping(target = "roles", source = "roleIds")
    @Mapping(target = "jlptLevel", defaultValue = "N5")
    @Mapping(target = "userSessions", ignore = true)
    @Mapping(target = "questions", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    @Mapping(target = "reports", ignore = true)
    @Mapping(target = "pointHistories", ignore = true)
    @Mapping(target = "userNodeProgresses", ignore = true)
    @Mapping(target = "userLearningProgress", ignore = true)
    @Mapping(target = "oAuthProviders", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "userDailyAttendances", ignore = true)
    @Mapping(target = "isEmailVerified", source = "emailVerified")
    UserEntity domainToEntity(User user);

    @Named("getOAuthProviderIds")
    default List<Long> getOAuthProviderIds(List<OAuthProviderEntity> oAuthProviders) {
        return oAuthProviders.stream().map(OAuthProviderEntity::getId).toList();
    }

    @Named("getOAuthProviderAvatarUrls")
    default List<String> getOAuthProviderAvatarUrl(List<OAuthProviderEntity> oAuthProviders) {
        return oAuthProviders.stream().map(OAuthProviderEntity::getAvatarUrl).toList();
    }
}
