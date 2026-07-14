package org.naho.user.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.point.dto.mapper.PointSummaryResponseMapper;
import org.naho.user.dto.response.UserResponse;
import org.naho.user.result.UserResult;

@Mapper(
        componentModel = "spring",
        uses = {PointSummaryResponseMapper.class, OAuthProviderResponseMapper.class}
)
public interface UserResponseMapper {
    UserResponse resultToResponse(UserResult result);
}
