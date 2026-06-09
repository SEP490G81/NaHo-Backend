package org.naho.speech.topic.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.speech.TopicDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.speech.topic.command.ListTopicCommand;
import org.naho.speech.topic.dto.mapper.TopicRequestMapper;
import org.naho.speech.topic.dto.mapper.TopicResponseMapper;
import org.naho.speech.topic.dto.request.CreateTopicRequest;
import org.naho.speech.topic.dto.request.TopicFilterRequest;
import org.naho.speech.topic.dto.response.CreateTopicResponse;
import org.naho.speech.topic.dto.response.TopicListItemResponse;
import org.naho.speech.topic.port.in.CreateTopicInputPort;
import org.naho.speech.topic.port.in.ListTopicInputPort;
import org.naho.speech.topic.result.CreateTopicResult;
import org.naho.speech.topic.result.TopicListResult;
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

    // CREATE TOPIC
    @PostMapping
    @ApiResponseMessage(message = TopicDetailMessageKey.TOPIC_CREATION_SUCCESS)
    public ResponseEntity<CreateTopicResponse> createTopic(
            @RequestBody CreateTopicRequest request,
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        Long userId = payload.userId();

        var command = topicRequestMapper.toCommand(request, userId);
        CreateTopicResult result = createTopicInputPort.createTopic(command);
        CreateTopicResponse response = topicResponseMapper.resultToResponse(result);

        return ResponseEntity.ok(response);
    }

    // GET LIST TOPIC
    @GetMapping
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
}
