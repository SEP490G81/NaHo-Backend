package org.naho.book.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.book.dto.response.ObjectiveDetailResponse;
import org.naho.book.result.ObjectiveDetailResult;
import org.naho.learning.dto.response.LearningPathNodeListItemResponse;
import org.naho.learning.result.LearningPathNodeListItemResult;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ObjectiveResponseMapper {

    ObjectiveDetailResponse detailResultToResponse(ObjectiveDetailResult result);

    LearningPathNodeListItemResponse nodeResultToResponse(LearningPathNodeListItemResult result);

    List<LearningPathNodeListItemResponse> nodeResultListToResponse(List<LearningPathNodeListItemResult> results);
}
