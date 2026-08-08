package org.naho.user.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.naho.user.entity.UserEntity;
import org.naho.user.result.LeaderboardUserResult;
import org.naho.user.type.AuthProviderName;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserQueryMapper {
    Optional<UserEntity> findByProviderUserIdAndProviderName(
            @Param("providerUserId") String providerUserId,
            @Param("providerName") AuthProviderName providerName
    );

    List<LeaderboardUserResult> findTop10OrderByTotalPointInLeague(@Param("leagueId") Long leagueId);

    Optional<LeaderboardUserResult> findTopOfUserByUserId(@Param("userId") Long userId);
}
