package org.naho.question.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.naho.file.dto.mapper.FileResponseMapper;
import org.naho.question.command.SpeakingHistoryFilterCommand;
import org.naho.question.constant.SpeakingHistorySortColumn;
import org.naho.question.dto.request.SpeakingHistoryFilterRequest;
import org.naho.question.dto.request.SpeakingHistoryQueryRequest;
import org.naho.question.dto.response.SpeakingHistoryDetailResponse;
import org.naho.question.dto.response.SpeakingHistoryListItemResponse;
import org.naho.question.result.SpeakingHistoryDetailResult;
import org.naho.question.result.SpeakingHistoryListItemResult;
import org.naho.shared.constant.SortDirection;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring", uses = {
        FileResponseMapper.class
})
public interface AnswerHistoryResponseMapper {

    default SpeakingHistoryFilterCommand requestToCommand(SpeakingHistoryQueryRequest request, Long userId) {
        if (request == null) {
            return new SpeakingHistoryFilterCommand(userId, null, null, null, 0, 10, SpeakingHistorySortColumn.CREATED_TIME, SortDirection.DESC);
        }
        return new SpeakingHistoryFilterCommand(
                userId,
                request.getSpeakingQuestionId(),
                request.getTopicId(),
                request.getSearch(),
                request.getPage() != null ? request.getPage() : 0,
                request.getSize() != null ? request.getSize() : 10,
                request.getSortColumn() != null ? request.getSortColumn() : SpeakingHistorySortColumn.CREATED_TIME,
                request.getSortDirection() != null ? request.getSortDirection() : SortDirection.DESC
        );
    }

    default SpeakingHistoryFilterCommand requestToCommand(SpeakingHistoryFilterRequest request, Long userId) {
        if (request == null) {
            return new SpeakingHistoryFilterCommand(userId, null, null, null, 0, 10, SpeakingHistorySortColumn.CREATED_TIME, SortDirection.DESC);
        }
        return new SpeakingHistoryFilterCommand(
                userId,
                request.speakingQuestionId(),
                request.topicId(),
                request.search(),
                request.page() != null ? request.page() : 0,
                request.size() != null ? request.size() : 10,
                SpeakingHistorySortColumn.CREATED_TIME,
                SortDirection.DESC
        );
    }

    @Mapping(target = "practicedAt", source = "practicedAt", qualifiedByName = "formatInstant")
    SpeakingHistoryListItemResponse toListItemResponse(SpeakingHistoryListItemResult result);

    @Mapping(target = "practicedAt", source = "practicedAt", qualifiedByName = "formatInstant")
    SpeakingHistoryDetailResponse toDetailResponse(SpeakingHistoryDetailResult result);

    @Named("formatInstant")
    default String formatInstant(Instant instant) {
        return instant != null ? DateTimeFormatter.ISO_INSTANT.format(instant) : null;
    }
}
