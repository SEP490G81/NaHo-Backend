package org.naho.book.controller.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.naho.book.dto.mapper.TopicRequestMapper;
import org.naho.book.dto.mapper.TopicResponseMapper;
import org.naho.book.dto.request.UpdateTopicRequest;
import org.naho.book.dto.response.TopicDetailResponse;
import org.naho.book.dto.response.TopicResponse;
import org.naho.book.port.in.GetTopicDetailInputPort;
import org.naho.book.port.in.ListTopicInputPort;
import org.naho.book.port.in.UpdateTopicInputPort;
import org.naho.book.result.TopicResult;
import org.naho.i18n.message.book.TopicDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.user.port.out.RoleRepositoryPort;
import org.naho.user.result.AccessTokenPayload;
import org.naho.user.type.RoleName;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/topics")
@RequiredArgsConstructor
public class TopicController {

    private final ListTopicInputPort listTopicInputPort;
    private final GetTopicDetailInputPort getTopicDetailInputPort;
    private final UpdateTopicInputPort updateTopicInputPort;
    private final TopicRequestMapper topicRequestMapper;
    private final TopicResponseMapper topicResponseMapper;
    private final RoleRepositoryPort roleRepositoryPort;


    // ROLE: ADMIN, CONTENT_MANAGER, LEARNER
    // FIND ALL TOPICS BY BOOK
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTENT_MANAGER', 'LEARNER')")
    @GetMapping("/books/{bookId}")
    @ApiResponseMessage(message = TopicDetailMessageKey.TOPIC_GET_LIST_SUCCESS)
    public ResponseEntity<List<TopicResponse>> listTopics(@PathVariable Long bookId) {
        List<TopicResult> results = listTopicInputPort.findAllByBookId(bookId);
        List<TopicResponse> responses = results
                .stream()
                .map(topicResponseMapper::resultToResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    // ROLE: ADMIN, CONTENT_MANAGER, LEARNER
    // GET TOPIC DETAIL
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTENT_MANAGER', 'LEARNER')")
    @GetMapping("/{id}")
    @ApiResponseMessage(message = TopicDetailMessageKey.TOPIC_GET_DETAIL_SUCCESS)
    public ResponseEntity<TopicDetailResponse> getTopicDetail(
            @PathVariable Long id
    ) {
        var command = topicRequestMapper.toDetailCommand(id);
        var result = getTopicDetailInputPort.getTopicDetail(command);
        var response = topicResponseMapper.detailResultToResponse(result);
        return ResponseEntity.ok(response);
    }

    // ROLE: CONTENT_MANAGER
    // UPDATE TOPIC
    @PreAuthorize("hasRole('CONTENT_MANAGER')")
    @PutMapping("/{id}")
    @ApiResponseMessage(message = TopicDetailMessageKey.TOPIC_UPDATE_SUCCESS)
    public ResponseEntity<TopicDetailResponse> updateTopic(
            @PathVariable Long id,
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

}
