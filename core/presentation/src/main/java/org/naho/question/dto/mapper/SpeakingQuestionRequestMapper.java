package org.naho.question.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.question.command.*;
import org.naho.question.dto.request.ChangeSpeakingQuestionStatusRequest;
import org.naho.question.dto.request.CreateSpeakingQuestionRequest;
import org.naho.question.dto.request.SpeakingQuestionFilterRequest;
import org.naho.question.dto.request.UpdateSpeakingQuestionRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Mapper(componentModel = "spring")
public interface SpeakingQuestionRequestMapper {

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "isContentManager", source = "isContentManager")
    CreateSpeakingQuestionCommand toCreateCommand(CreateSpeakingQuestionRequest request, Long userId, boolean isContentManager);

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "isContentManager", source = "isContentManager")
    UpdateSpeakingQuestionCommand toUpdateCommand(UpdateSpeakingQuestionRequest request, Long id, Long userId, boolean isContentManager);

    @Mapping(target = "userId", source = "requestUserId")
    DeleteSpeakingQuestionCommand toDeleteCommand(Long id, Long requestUserId, boolean isAdminOrManager);

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "questionId", source = "id")
    @Mapping(target = "isAdminOrManager", source = "isAdminOrManager")
    ChangeSpeakingQuestionStatusCommand toChangeStatusCommand(ChangeSpeakingQuestionStatusRequest request, Long id, Long userId, boolean isAdminOrManager);

    default SearchSpeakingQuestionsCommand toSearchCommand(
            SpeakingQuestionFilterRequest filter,
            Pageable pageable
    ) {
        String sortBy = "created_time";
        String sortDirection = "DESC";
        if (pageable.getSort().isSorted()) {
            Sort.Order order = pageable.getSort().iterator().next();
            sortBy = order.getProperty();
            sortDirection = order.getDirection().name();

            sortBy = switch (sortBy) {
                case "id" -> "id";
                case "createdTime", "createdAt" -> "created_time";
                case "title" -> "title";
                case "status" -> "status";
                default -> "created_time";
            };
        }

        int oneBasedPage = pageable.getPageNumber() + 1;

        return new SearchSpeakingQuestionsCommand(
                oneBasedPage,
                pageable.getPageSize(),
                filter != null ? filter.creatorId() : null,
                filter != null ? filter.isSystemCreated() : null,
                filter != null ? filter.statuses() : null,
                filter != null ? filter.keyword() : null,
                sortBy,
                sortDirection
        );
    }
}
