package org.naho.learning.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.learning.dto.response.UserNodeProgressResponse;
import org.naho.learning.result.UserNodeProgressResult;

@Mapper(componentModel = "spring")
public interface UserNodeProgressResponseMapper {
    UserNodeProgressResponse resultToResponse(UserNodeProgressResult result);
}
