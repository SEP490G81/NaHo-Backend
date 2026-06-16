package org.naho.topic.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.topic.command.*;
import org.naho.topic.dto.request.CreateTopicRequest;
import org.naho.topic.dto.request.TopicFilterRequest;
import org.naho.topic.dto.request.UpdateTopicRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Mapper(componentModel = "spring")
public interface TopicRequestMapper {

    @Mapping(target = "userId", source = "userId")
    CreateTopicCommand toCreateCommand(CreateTopicRequest request, Long userId);

    default ListTopicCommand toListCommand(
            TopicFilterRequest filter,
            Pageable pageable,
            boolean isAdmin
    ) {
        String sortBy = "order_index";
        String sortDirection = "ASC";
        if (pageable.getSort().isSorted()) {
            Sort.Order order = pageable.getSort().iterator().next();
            sortBy = order.getProperty();
            sortDirection = order.getDirection().name();
        }

        int oneBasedPage = pageable.getPageNumber() + 1;

        return new ListTopicCommand(
                oneBasedPage,
                pageable.getPageSize(),
                filter != null ? filter.keyword() : null,
                filter != null ? filter.status() : null,
                filter != null ? filter.jlptLevel() : null,
                filter != null ? filter.categoryId() : null,
                sortBy,
                sortDirection,
                isAdmin
        );
    }

    GetTopicDetailCommand toDetailCommand(Long id);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "requestUserId", source = "userId")
    @Mapping(target = "isAdminOrManager", source = "isAdminOrManager")
    UpdateTopicCommand toUpdateCommand(UpdateTopicRequest request, Long id, Long userId, boolean isAdminOrManager);

    DeleteTopicCommand toDeleteCommand(Long id, boolean isAdminOrManager);
}
