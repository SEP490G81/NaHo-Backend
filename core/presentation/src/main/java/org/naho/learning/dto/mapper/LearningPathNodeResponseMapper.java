package org.naho.learning.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.learning.dto.response.LearningPathNodeDetailResponse;
import org.naho.learning.result.LearningPathNodeDetailResult;

@Mapper(componentModel = "spring")
public interface LearningPathNodeResponseMapper {
    LearningPathNodeDetailResponse detailResultToResponse(LearningPathNodeDetailResult result);
}
