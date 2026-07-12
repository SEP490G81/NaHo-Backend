package org.naho.book.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.book.dto.response.LessonDetailResponse;
import org.naho.book.dto.response.ObjectiveResponse;
import org.naho.book.result.LessonDetailResult;
import org.naho.book.result.ObjectiveListItemResult;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LessonResponseMapper {

    LessonDetailResponse detailResultToResponse(LessonDetailResult result);

    ObjectiveResponse objectiveResultToResponse(ObjectiveListItemResult result);

    List<ObjectiveResponse> objectiveResultListToResponse(List<ObjectiveListItemResult> results);
}
