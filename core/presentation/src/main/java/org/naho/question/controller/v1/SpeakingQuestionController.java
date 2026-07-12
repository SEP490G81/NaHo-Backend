package org.naho.question.controller.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.question.SpeakingQuestionDetailMessageKey;
import org.naho.question.command.*;
import org.naho.question.dto.mapper.SpeakingQuestionRequestMapper;
import org.naho.question.dto.mapper.SpeakingQuestionResponseMapper;
import org.naho.question.dto.request.ChangeSpeakingQuestionStatusRequest;
import org.naho.question.dto.request.CreateSpeakingQuestionRequest;
import org.naho.question.dto.request.SpeakingQuestionFilterRequest;
import org.naho.question.dto.request.UpdateSpeakingQuestionRequest;
import org.naho.question.dto.response.SpeakingQuestionDetailResponse;
import org.naho.question.dto.response.SpeakingQuestionListItemResponse;
import org.naho.question.port.in.*;
import org.naho.question.result.CreateSpeakingQuestionResult;
import org.naho.question.result.SearchSpeakingQuestionsResult;
import org.naho.question.result.UpdateSpeakingQuestionResult;
import org.naho.shared.annotation.ApiResponseMessage;
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
@RequestMapping("/api/v1/speaking-questions")
@RequiredArgsConstructor
public class SpeakingQuestionController {

    private final CreateSpeakingQuestionInputPort createSpeakingQuestionInputPort;
    private final UpdateSpeakingQuestionInputPort updateSpeakingQuestionInputPort;
    private final DeleteSpeakingQuestionInputPort deleteSpeakingQuestionInputPort;
    private final ChangeSpeakingQuestionStatusInputPort changeSpeakingQuestionStatusInputPort;
    private final SearchSpeakingQuestionsInputPort searchSpeakingQuestionsInputPort;
    private final RoleRepositoryPort roleRepositoryPort;
    private final SpeakingQuestionRequestMapper speakingQuestionRequestMapper;
    private final SpeakingQuestionResponseMapper speakingQuestionResponseMapper;

    @PostMapping
    @ApiResponseMessage(message = SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_CREATION_SUCCESS)
    public ResponseEntity<SpeakingQuestionDetailResponse> createSpeakingQuestion(
            @RequestBody @Valid CreateSpeakingQuestionRequest request,
            @AuthenticationPrincipal AccessTokenPayload payload) {
        Long currentUserId = payload.userId();
        List<String> roleSet = roleRepositoryPort.findRoleNamesByUserId(currentUserId);
        boolean isContentManager = roleSet.contains(RoleName.ADMIN.name()) || roleSet.contains(RoleName.CONTENT_MANAGER.name());

        CreateSpeakingQuestionCommand command = speakingQuestionRequestMapper.toCreateCommand(request, currentUserId, isContentManager);

        CreateSpeakingQuestionResult result = createSpeakingQuestionInputPort.createSpeakingQuestion(command);
        SpeakingQuestionDetailResponse response = speakingQuestionResponseMapper.createResultToDetailResponse(result);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @ApiResponseMessage(message = SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_UPDATE_SUCCESS)
    public ResponseEntity<SpeakingQuestionDetailResponse> updateSpeakingQuestion(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateSpeakingQuestionRequest request,
            @AuthenticationPrincipal AccessTokenPayload payload) {
        Long currentUserId = payload.userId();
        List<String> roleSet = roleRepositoryPort.findRoleNamesByUserId(currentUserId);
        boolean isContentManager = roleSet.contains(RoleName.ADMIN.name()) || roleSet.contains(RoleName.CONTENT_MANAGER.name());

        UpdateSpeakingQuestionCommand command = speakingQuestionRequestMapper.toUpdateCommand(request, id, currentUserId, isContentManager);

        UpdateSpeakingQuestionResult result = updateSpeakingQuestionInputPort.updateSpeakingQuestion(command);
        SpeakingQuestionDetailResponse response = speakingQuestionResponseMapper.updateResultToDetailResponse(result);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @ApiResponseMessage(message = SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_DELETE_SUCCESS)
    public ResponseEntity<Void> deleteSpeakingQuestion(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal AccessTokenPayload payload) {
        Long currentUserId = payload.userId();
        List<String> roleSet = roleRepositoryPort.findRoleNamesByUserId(currentUserId);
        boolean isAdminOrManager = roleSet.contains(RoleName.ADMIN.name()) || roleSet.contains(RoleName.CONTENT_MANAGER.name());

        DeleteSpeakingQuestionCommand command = speakingQuestionRequestMapper.toDeleteCommand(id, currentUserId, isAdminOrManager);
        deleteSpeakingQuestionInputPort.deleteSpeakingQuestion(command);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    @ApiResponseMessage(message = SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_UPDATE_SUCCESS)
    public ResponseEntity<Void> changeStatus(
            @PathVariable("id") Long id,
            @RequestBody @Valid ChangeSpeakingQuestionStatusRequest request,
            @AuthenticationPrincipal AccessTokenPayload payload) {
        Long currentUserId = payload.userId();
        List<String> roleSet = roleRepositoryPort.findRoleNamesByUserId(currentUserId);
        boolean isAdminOrManager = roleSet.contains(RoleName.ADMIN.name()) || roleSet.contains(RoleName.CONTENT_MANAGER.name());

        ChangeSpeakingQuestionStatusCommand command = speakingQuestionRequestMapper.toChangeStatusCommand(request, id, currentUserId, isAdminOrManager);
        changeSpeakingQuestionStatusInputPort.changeSpeakingQuestionStatus(command);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @ApiResponseMessage(message = SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_GET_LIST_SUCCESS)
    public ResponseEntity<Page<SpeakingQuestionListItemResponse>> searchSpeakingQuestions(
            @ModelAttribute SpeakingQuestionFilterRequest filter,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal AccessTokenPayload payload) {

        SearchSpeakingQuestionsCommand command = speakingQuestionRequestMapper.toSearchCommand(filter, pageable);

        SearchSpeakingQuestionsResult result = searchSpeakingQuestionsInputPort.searchSpeakingQuestions(command);
        List<SpeakingQuestionListItemResponse> items = speakingQuestionResponseMapper.listResultToResponse(result.items());

        return ResponseEntity.ok(new PageImpl<>(items, pageable, result.totalElements()));
    }
}
