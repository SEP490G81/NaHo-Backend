package org.naho.user.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.user.dto.response.LeaderboardUserResponse;
import org.naho.user.dto.response.UserResponse;
import org.naho.user.result.LeaderboardUserResult;
import org.naho.user.result.UserResult;

@Mapper(
        componentModel = "spring",
        uses = {OAuthProviderResponseMapper.class}
)
public interface UserResponseMapper {
    UserResponse resultToResponse(UserResult result);

    LeaderboardUserResponse resultToResponse(LeaderboardUserResult result);
}
