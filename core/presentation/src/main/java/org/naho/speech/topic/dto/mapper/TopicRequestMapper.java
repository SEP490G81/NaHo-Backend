package org.naho.speech.topic.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.shared.mapper.JsonMapper;
import org.naho.speech.topic.command.CreateTopicCommand;
import org.naho.speech.topic.command.GetTopicDetailCommand;
import org.naho.speech.topic.command.ListTopicCommand;
import org.naho.speech.topic.command.UpdateTopicCommand;
import org.naho.speech.topic.dto.request.CreateTopicRequest;
import org.naho.speech.topic.dto.request.TopicFilterRequest;
import org.naho.speech.topic.dto.request.UpdateTopicRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Mapper(componentModel = "spring", uses = {JsonMapper.class})
public interface TopicRequestMapper {

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "nameTokens", source = "request.nameTokens", qualifiedByName = "convertObjectToJsonString")
    @Mapping(target = "descriptionTokens", source = "request.descriptionTokens", qualifiedByName = "convertObjectToJsonString")
    CreateTopicCommand toCommand(CreateTopicRequest request, Long userId);

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
                sortBy,
                sortDirection,
                isAdmin
        );
    }

    GetTopicDetailCommand toDetailCommand(Long id);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "requestUserId", source = "userId")
    @Mapping(target = "isAdminOrManager", source = "isAdminOrManager")
    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "nameTokens", source = "request.nameTokens", qualifiedByName = "convertObjectToJsonString")
    @Mapping(target = "descriptionTokens", source = "request.descriptionTokens", qualifiedByName = "convertObjectToJsonString")
    @Mapping(target = "status", source = "request.status")
    @Mapping(target = "jlptLevel", source = "request.jlptLevel")
    @Mapping(target = "orderIndex", source = "request.orderIndex")
    @Mapping(target = "coverImageFileId", source = "request.coverImageFileId")
    UpdateTopicCommand toUpdateCommand(UpdateTopicRequest request, Long id, Long userId, boolean isAdminOrManager);
}
