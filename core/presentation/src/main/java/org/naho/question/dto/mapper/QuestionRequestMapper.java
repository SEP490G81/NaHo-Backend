package org.naho.question.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.question.command.*;
import org.naho.question.dto.request.ChangeQuestionStatusRequest;
import org.naho.question.dto.request.CreateQuestionRequest;
import org.naho.question.dto.request.QuestionFilterRequest;
import org.naho.question.dto.request.UpdateQuestionRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Mapper(componentModel = "spring")
public interface QuestionRequestMapper {

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "isContentManager", source = "isContentManager")
    CreateQuestionCommand toCreateCommand(CreateQuestionRequest request, Long userId, boolean isContentManager);

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "isContentManager", source = "isContentManager")
    UpdateQuestionCommand toUpdateCommand(UpdateQuestionRequest request, Long id, Long userId, boolean isContentManager);

    @Mapping(target = "userId", source = "requestUserId")
    DeleteQuestionCommand toDeleteCommand(Long id, Long requestUserId, boolean isAdminOrManager);

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "questionId", source = "id")
    @Mapping(target = "isAdminOrManager", source = "isAdminOrManager")
    ChangeQuestionStatusCommand toChangeStatusCommand(ChangeQuestionStatusRequest request, Long id, Long userId, boolean isAdminOrManager);

    default SearchQuestionsCommand toSearchCommand(
            QuestionFilterRequest filter,
            Pageable pageable
    ) {
        String sortBy = "order_index";
        String sortDirection = "ASC";
        if (pageable.getSort().isSorted()) {
            Sort.Order order = pageable.getSort().iterator().next();
            sortBy = order.getProperty();
            sortDirection = order.getDirection().name();

            // Map safely from request property to Database column name
            // Prevent SQL Injection when concatenating strings directly in MyBatis
            sortBy = switch (sortBy) {
                case "id" -> "id";
                case "createdTime", "createdAt" -> "created_time";
                case "title" -> "title";
                case "status" -> "status";
                default -> "order_index";
            };
        }

        int oneBasedPage = pageable.getPageNumber() + 1;

        return new SearchQuestionsCommand(
                oneBasedPage,
                pageable.getPageSize(),
                filter != null ? filter.topicId() : null,
                filter != null ? filter.creatorId() : null,
                filter != null ? filter.isSystemCreated() : null,
                filter != null ? filter.statuses() : null,
                filter != null ? filter.keyword() : null,
                sortBy,
                sortDirection
        );
    }
}
