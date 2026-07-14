package org.naho.user.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.naho.user.entity.UserEntity;
import org.naho.user.type.OAuthProviderName;

import java.util.Optional;

@Mapper
public interface UserQueryMapper {
    Optional<UserEntity> findByProviderUserIdAndProviderName(
            @Param("providerUserId") String providerUserId,
            @Param("providerName") OAuthProviderName providerName
    );
}
