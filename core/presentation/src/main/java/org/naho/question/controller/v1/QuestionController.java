package org.naho.question.controller.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.question.QuestionDetailMessageKey;
import org.naho.question.command.*;
import org.naho.question.dto.mapper.QuestionRequestMapper;
import org.naho.question.dto.mapper.QuestionResponseMapper;
import org.naho.question.dto.request.ChangeQuestionStatusRequest;
import org.naho.question.dto.request.CreateQuestionRequest;
import org.naho.question.dto.request.QuestionFilterRequest;
import org.naho.question.dto.request.UpdateQuestionRequest;
import org.naho.question.dto.response.QuestionDetailResponse;
import org.naho.question.dto.response.QuestionListItemResponse;
import org.naho.question.port.in.*;
import org.naho.question.result.CreateQuestionResult;
import org.naho.question.result.SearchQuestionsResult;
import org.naho.question.result.UpdateQuestionResult;
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
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final CreateQuestionInputPort createQuestionInputPort;
    private final UpdateQuestionInputPort updateQuestionInputPort;
    private final DeleteQuestionInputPort deleteQuestionInputPort;
    private final ChangeQuestionStatusInputPort changeQuestionStatusInputPort;
    private final SearchQuestionsInputPort searchQuestionsInputPort;
    private final RoleRepositoryPort roleRepositoryPort;
    private final QuestionRequestMapper questionRequestMapper;
    private final QuestionResponseMapper questionResponseMapper;

    @PostMapping
    @ApiResponseMessage(message = QuestionDetailMessageKey.QUESTION_CREATION_SUCCESS)
    public ResponseEntity<QuestionDetailResponse> createQuestion(
            @RequestBody @Valid CreateQuestionRequest request,
            @AuthenticationPrincipal AccessTokenPayload payload) {
        Long currentUserId = payload.userId();
        List<String> roleSet = roleRepositoryPort.findRoleNamesByUserId(currentUserId);
        boolean isContentManager = roleSet.contains(RoleName.ADMIN.name()) || roleSet.contains(RoleName.CONTENT_MANAGER.name());

        CreateQuestionCommand command = questionRequestMapper.toCreateCommand(request, currentUserId, isContentManager);

        CreateQuestionResult result = createQuestionInputPort.createQuestion(command);
        QuestionDetailResponse response = questionResponseMapper.createResultToDetailResponse(result);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @ApiResponseMessage(message = QuestionDetailMessageKey.QUESTION_UPDATE_SUCCESS)
    public ResponseEntity<QuestionDetailResponse> updateQuestion(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateQuestionRequest request,
            @AuthenticationPrincipal AccessTokenPayload payload) {
        Long currentUserId = payload.userId();
        List<String> roleSet = roleRepositoryPort.findRoleNamesByUserId(currentUserId);
        boolean isContentManager = roleSet.contains(RoleName.ADMIN.name()) || roleSet.contains(RoleName.CONTENT_MANAGER.name());

        UpdateQuestionCommand command = questionRequestMapper.toUpdateCommand(request, id, currentUserId, isContentManager);

        UpdateQuestionResult result = updateQuestionInputPort.updateQuestion(command);
        QuestionDetailResponse response = questionResponseMapper.updateResultToDetailResponse(result);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @ApiResponseMessage(message = QuestionDetailMessageKey.QUESTION_DELETE_SUCCESS)
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal AccessTokenPayload payload) {
        Long currentUserId = payload.userId();
        List<String> roleSet = roleRepositoryPort.findRoleNamesByUserId(currentUserId);
        boolean isAdminOrManager = roleSet.contains(RoleName.ADMIN.name()) || roleSet.contains(RoleName.CONTENT_MANAGER.name());

        DeleteQuestionCommand command = questionRequestMapper.toDeleteCommand(id, currentUserId, isAdminOrManager);
        deleteQuestionInputPort.deleteQuestion(command);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    @ApiResponseMessage(message = QuestionDetailMessageKey.QUESTION_UPDATE_SUCCESS)
    public ResponseEntity<Void> changeStatus(
            @PathVariable("id") Long id,
            @RequestBody @Valid ChangeQuestionStatusRequest request,
            @AuthenticationPrincipal AccessTokenPayload payload) {
        Long currentUserId = payload.userId();
        List<String> roleSet = roleRepositoryPort.findRoleNamesByUserId(currentUserId);
        boolean isAdminOrManager = roleSet.contains(RoleName.ADMIN.name()) || roleSet.contains(RoleName.CONTENT_MANAGER.name());

        ChangeQuestionStatusCommand command = questionRequestMapper.toChangeStatusCommand(request, id, currentUserId, isAdminOrManager);
        changeQuestionStatusInputPort.changeQuestionStatus(command);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @ApiResponseMessage(message = QuestionDetailMessageKey.QUESTION_GET_LIST_SUCCESS)
    public ResponseEntity<Page<QuestionListItemResponse>> searchQuestions(
            @ModelAttribute QuestionFilterRequest filter,
            @PageableDefault(page = 0, size = 10, sort = "order_index", direction = Sort.Direction.ASC) Pageable pageable,
            @AuthenticationPrincipal AccessTokenPayload payload) {

        SearchQuestionsCommand command = questionRequestMapper.toSearchCommand(filter, pageable);

        SearchQuestionsResult result = searchQuestionsInputPort.searchQuestions(command);
        List<QuestionListItemResponse> items = questionResponseMapper.listResultToResponse(result.items());

        return ResponseEntity.ok(new PageImpl<>(items, pageable, result.totalElements()));
    }
}
