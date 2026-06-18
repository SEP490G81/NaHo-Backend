package org.naho.topic.controller.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.topic.TopicDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.topic.command.ListTopicCommand;
import org.naho.topic.dto.mapper.TopicRequestMapper;
import org.naho.topic.dto.mapper.TopicResponseMapper;
import org.naho.topic.dto.request.CreateTopicRequest;
import org.naho.topic.dto.request.TopicFilterRequest;
import org.naho.topic.dto.request.UpdateTopicRequest;
import org.naho.topic.dto.response.CreateTopicResponse;
import org.naho.topic.dto.response.TopicDetailResponse;
import org.naho.topic.dto.response.TopicListItemResponse;
import org.naho.topic.port.in.*;
import org.naho.topic.result.CreateTopicResult;
import org.naho.topic.result.TopicListResult;
import org.naho.user.port.out.RoleRepositoryPort;
import org.naho.user.result.AccessTokenPayload;
import org.naho.user.type.RoleName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/topics")
@RequiredArgsConstructor
public class TopicController {

    private final CreateTopicInputPort createTopicInputPort;
    private final TopicRequestMapper topicRequestMapper;
    private final TopicResponseMapper topicResponseMapper;
    private final RoleRepositoryPort roleRepositoryPort;

    private final ListTopicInputPort listTopicInputPort;
    private final GetTopicDetailInputPort getTopicDetailInputPort;
    private final UpdateTopicInputPort updateTopicInputPort;
    private final DeleteTopicInputPort deleteTopicInputPort;

    // CREATE TOPIC
    @PostMapping
    @ApiResponseMessage(message = TopicDetailMessageKey.TOPIC_CREATION_SUCCESS)
    public ResponseEntity<CreateTopicResponse> createTopic(
            @RequestBody CreateTopicRequest request,
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        Long userId = payload.userId();

        var command = topicRequestMapper.toCreateCommand(request, userId);
        CreateTopicResult result = createTopicInputPort.createTopic(command);
        CreateTopicResponse response = topicResponseMapper.createResultToResponse(result);

        return ResponseEntity.ok(response);
    }

    // GET LIST TOPIC
    @GetMapping
    @ApiResponseMessage(message = TopicDetailMessageKey.TOPIC_GET_LIST_SUCCESS)
    public ResponseEntity<Page<TopicListItemResponse>> listTopics(
            @ModelAttribute TopicFilterRequest filter,
            @PageableDefault(page = 0, size = 10, sort = "order_index", direction = Sort.Direction.ASC) Pageable pageable,
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        List<String> roleSet = roleRepositoryPort.findRoleNamesByUserId(payload.userId());
        boolean isAdmin = roleSet.contains(RoleName.ADMIN.name()) ||
                roleSet.contains(RoleName.CONTENT_MANAGER.name());

        ListTopicCommand query = topicRequestMapper.toListCommand(filter, pageable, isAdmin);
        TopicListResult result = listTopicInputPort.listTopics(query);
        List<TopicListItemResponse> items = topicResponseMapper.listResultToResponse(result.items());

        return ResponseEntity.ok(new PageImpl<>(items, pageable, result.totalElements()));
    }


    // GET TOPIC DETAIL
    @GetMapping("/{id}")
    @ApiResponseMessage(message = TopicDetailMessageKey.TOPIC_GET_DETAIL_SUCCESS)
    public ResponseEntity<TopicDetailResponse> getTopicDetail(
            @PathVariable("id") Long id
    ) {
        var command = topicRequestMapper.toDetailCommand(id);
        var result = getTopicDetailInputPort.getTopicDetail(command);
        var response = topicResponseMapper.detailResultToResponse(result);
        return ResponseEntity.ok(response);
    }

    // UPDATE TOPIC
    @PutMapping("/{id}")
    @ApiResponseMessage(message = TopicDetailMessageKey.TOPIC_UPDATE_SUCCESS)
    public ResponseEntity<TopicDetailResponse> updateTopic(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateTopicRequest request,
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        List<String> roleSet = roleRepositoryPort.findRoleNamesByUserId(payload.userId());
        boolean isAdminOrManager =
                roleSet.contains(RoleName.ADMIN.name()) ||
                        roleSet.contains(RoleName.CONTENT_MANAGER.name());

        var command = topicRequestMapper.toUpdateCommand(request, id, payload.userId(), isAdminOrManager);
        var result = updateTopicInputPort.updateTopic(command);
        var response = topicResponseMapper.detailResultToResponse(result);
        return ResponseEntity.ok(response);
    }

    // DELETE TOPIC
    @DeleteMapping("/{id}")
    @ApiResponseMessage(message = TopicDetailMessageKey.TOPIC_DELETE_SUCCESS)
    public ResponseEntity<Void> deleteTopic(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        List<String> roleSet = roleRepositoryPort.findRoleNamesByUserId(payload.userId());
        boolean isAdminOrManager =
                roleSet.contains(RoleName.ADMIN.name()) ||
                        roleSet.contains(RoleName.CONTENT_MANAGER.name());

        var command = topicRequestMapper.toDeleteCommand(id, isAdminOrManager);
        deleteTopicInputPort.deleteTopic(command);
        return ResponseEntity.noContent().build();
    }
}
