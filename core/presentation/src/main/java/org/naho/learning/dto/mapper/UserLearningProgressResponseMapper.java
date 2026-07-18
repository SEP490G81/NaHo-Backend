package org.naho.learning.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.learning.dto.response.UserLearningProgressResponse;
import org.naho.learning.result.UserLearningProgressResult;
import org.naho.user.dto.mapper.UserResponseMapper;

@Mapper(
        componentModel = "spring",
        uses = {UserResponseMapper.class}
)
public interface UserLearningProgressResponseMapper {

    UserLearningProgressResponse resultToResponse(UserLearningProgressResult result);
}
