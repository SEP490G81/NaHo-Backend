package org.naho.speech.topic.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.speech.TopicDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.speech.topic.command.ListTopicCommand;
import org.naho.speech.topic.dto.mapper.TopicRequestMapper;
import org.naho.speech.topic.dto.mapper.TopicResponseMapper;
import org.naho.speech.topic.dto.request.CreateTopicRequest;
import org.naho.speech.topic.dto.response.TopicListItemResponse;
import org.naho.speech.topic.dto.response.TopicResponse;
import org.naho.speech.topic.port.in.CreateTopicInputPort;
import org.naho.speech.topic.port.in.ListTopicUseCasePort;
import org.naho.speech.topic.result.TopicListResult;
import org.naho.speech.topic.result.TopicResult;
import org.naho.speech.type.TopicStatus;
import org.naho.user.port.out.RoleRepositoryPort;
import org.naho.user.result.AccessTokenPayload;
import org.naho.user.type.JLPTLevel;
import org.naho.user.type.RoleName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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

    private final ListTopicUseCasePort listTopicUseCasePort;

    @PostMapping
    @ApiResponseMessage(message = TopicDetailMessageKey.TOPIC_CREATION_SUCCESS)
    public ResponseEntity<TopicResponse> createTopic(
            @RequestBody CreateTopicRequest request,
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        Long userId = payload.userId();

        var command = topicRequestMapper.toCommand(request, userId);
        TopicResult result = createTopicInputPort.createTopic(command);
        TopicResponse response = topicResponseMapper.resultToResponse(result);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<TopicListItemResponse>> listTopics(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "status", required = false) TopicStatus status,
            @RequestParam(name = "jlptLevel", required = false) JLPTLevel jlptLevel,
            @RequestParam(name = "sortBy", defaultValue = "order_index") String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection,
            @AuthenticationPrincipal AccessTokenPayload payload
//            @RequestHeader(name = "X-Role", defaultValue = "USER") String role
    ) {

        List<String> roleSet = roleRepositoryPort.findRoleNamesByUserId(payload.userId());
        boolean isAdmin = roleSet.contains(RoleName.ADMIN.name());

        ListTopicCommand query = new ListTopicCommand(
                page, size, keyword, status, jlptLevel, sortBy, sortDirection, isAdmin
        );

        TopicListResult result = listTopicUseCasePort.listTopics(query);
        List<TopicListItemResponse> items = topicResponseMapper.listResultToResponse(result.items());

        // We wrap it in a pseudo Page to utilize ApiResponseHandler's pagination capability
        Page<TopicListItemResponse> pageResult =
                new PageImpl<>(
                        items,
                        PageRequest.of(query.page() - 1, query.size()),
                        result.totalElements()
                );

        return ResponseEntity.ok(pageResult);
    }
}
