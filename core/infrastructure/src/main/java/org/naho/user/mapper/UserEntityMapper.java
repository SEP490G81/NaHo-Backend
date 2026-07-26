package org.naho.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.naho.learning.entity.UserLearningProgressEntity;
import org.naho.learning.repository.UserLearningProgressJpaRepository;
import org.naho.user.entity.OAuthProviderEntity;
import org.naho.user.entity.UserEntity;
import org.naho.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {
                RoleEntityMapper.class,
                UserValueObjectMapper.class
        }
)
public abstract class UserEntityMapper {

    @Autowired
    private UserLearningProgressJpaRepository userLearningProgressJpaRepository;

    @Mapping(target = "roleIds", source = "roles")
    @Mapping(target = "userLearningProgressId", source = "userLearningProgress.id")
    @Mapping(target = "userSessionIds", ignore = true)
    @Mapping(target = "oAuthProviderIds", source = "OAuthProviders", qualifiedByName = "getOAuthProviderIds")
    @Mapping(target = "avatarFileId", source = "avatar.id")
    public abstract User entityToDomain(UserEntity entity);

    @Mapping(target = "roles", source = "roleIds")
    @Mapping(target = "jlptLevel", defaultValue = "N5")
    @Mapping(
            target = "userLearningProgress",
            source = "userLearningProgressId",
            qualifiedByName = "getUserLearningProgressEntityByUserId"
    )
    @Mapping(target = "userSessions", ignore = true)
    @Mapping(target = "questions", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    @Mapping(target = "reports", ignore = true)
    @Mapping(target = "pointHistories", ignore = true)
    @Mapping(target = "userNodeProgresses", ignore = true)
    @Mapping(target = "oAuthProviders", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "userDailyAttendances", ignore = true)
    public abstract UserEntity domainToEntity(User user);

    @Named("getUserLearningProgressEntityByUserId")
    protected UserLearningProgressEntity getUserLearningProgressEntityById(Long userLearningProgressId) {
        if (userLearningProgressId == null) {
            return null;
        }
        return userLearningProgressJpaRepository.findById(userLearningProgressId)
                .orElse(null);
    }

    @Named("getOAuthProviderIds")
    protected List<Long> getOAuthProviderIds(List<OAuthProviderEntity> oAuthProviders) {
        return oAuthProviders.stream().map(OAuthProviderEntity::getId).toList();
    }

    @Named("getOAuthProviderAvatarUrls")
    protected List<String> getOAuthProviderAvatarUrl(List<OAuthProviderEntity> oAuthProviders) {
        return oAuthProviders.stream().map(OAuthProviderEntity::getAvatarUrl).toList();
    }
}
