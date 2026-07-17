package org.naho.learning.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.learning.dto.response.UserLearningProgressResponse;
import org.naho.learning.result.UserLearningProgressResult;

@Mapper(componentModel = "spring")
public interface UserLearningProgressResponseMapper {

    UserLearningProgressResponse resultToResponse(UserLearningProgressResult result);
}
