package org.naho.social.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.social.CommentDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.social.comment.command.CommentCreateCommand;
import org.naho.social.comment.command.CommentDeleteCommand;
import org.naho.social.comment.command.CommentReadCommand;
import org.naho.social.comment.command.CommentUpdateCommand;
import org.naho.social.comment.dto.mapper.CommentCommandMapper;
import org.naho.social.comment.dto.mapper.CommentResponseMapper;
import org.naho.social.comment.dto.request.CreateCommentRequest;
import org.naho.social.comment.dto.request.DeleteCommandRequest;
import org.naho.social.comment.dto.request.UpdateCommentRequest;
import org.naho.social.comment.dto.response.CommentListResponse;
import org.naho.social.comment.dto.response.CommentResponse;
import org.naho.social.comment.port.in.CommentCrudInputPort;
import org.naho.social.comment.result.CommentListResult;
import org.naho.social.comment.result.CommentResult;
import org.naho.user.port.out.RoleRepositoryPort;
import org.naho.user.result.AccessTokenPayload;
import org.naho.user.type.RoleName;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentCrudInputPort commentCrudInputPort;
    private final CommentCommandMapper commentCommandMapper;
    private final CommentResponseMapper commentResponseMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final RoleRepositoryPort roleRepositoryPort;

    @GetMapping
    @ApiResponseMessage(message = CommentDetailMessageKey.COMMENT_GET_LIST_SUCCESS)
    public ResponseEntity<CommentListResponse> getComments(
            @RequestParam("speakingQuestionId") Long speakingQuestionId,
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        Long currentUserId = payload != null ? payload.userId() : null;
        CommentReadCommand command = new CommentReadCommand(speakingQuestionId);
        CommentListResult result = commentCrudInputPort.getListCommentOfQuestion(command, currentUserId);
        CommentListResponse response = commentResponseMapper.resultToResponse(result);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CommentResponse> createCommentRest(
            @Valid @RequestBody CreateCommentRequest createCommentRequest,
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        Long userId = payload != null ? payload.userId() : createCommentRequest.userId();
        CommentCreateCommand createCommand = commentCommandMapper.requestToCommand(createCommentRequest, userId);
        CommentResult commentResult = commentCrudInputPort.createComment(createCommand);
        CommentResponse response = commentResponseMapper.resultToResponse(commentResult);
        messagingTemplate.convertAndSend("/topic/comments", response);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    @ApiResponseMessage(message = CommentDetailMessageKey.COMMENT_UPDATE_SUCCESS)
    public ResponseEntity<CommentResponse> updateCommentRest(
            @Valid @RequestBody UpdateCommentRequest updateCommentRequest,
            @AuthenticationPrincipal AccessTokenPayload payload) {
        Long userId = payload != null ? payload.userId() : null;
        CommentUpdateCommand command = commentCommandMapper.requestToUpdateCommand(updateCommentRequest, userId);
        CommentResult commentResult = commentCrudInputPort.updateComment(command);
        CommentResponse response = commentResponseMapper.resultToResponse(commentResult);
        messagingTemplate.convertAndSend("/topic/comments", response);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    @ApiResponseMessage(message = CommentDetailMessageKey.COMMENT_DELETE_SUCCESS)
    public ResponseEntity<Void> deleteCommentRest(
            @Valid @RequestBody DeleteCommandRequest deleteCommandRequest,
            @AuthenticationPrincipal AccessTokenPayload payload) {
        Long userId = payload != null ? payload.userId() : null;
        boolean isAdmin = checkIsAdmin(userId);
        CommentDeleteCommand command = commentCommandMapper.requestToDeleteCommand(deleteCommandRequest, userId, isAdmin);
        commentCrudInputPort.deleteComment(command);
        messagingTemplate.convertAndSend("/topic/comments", deleteCommandRequest);
        return ResponseEntity.ok().build();
    }

    @MessageMapping("/comments/create")
    @SendTo("/topic/comments")
    public CommentResponse createComment(
            @Valid @Payload CreateCommentRequest createCommentRequest,
            @AuthenticationPrincipal AccessTokenPayload payload) {
        Long userId = payload != null ? payload.userId() : createCommentRequest.userId();
        CommentCreateCommand createCommand = commentCommandMapper.requestToCommand(createCommentRequest, userId);
        CommentResult commentResult = commentCrudInputPort.createComment(createCommand);
        return commentResponseMapper.resultToResponse(commentResult);
    }

    @MessageMapping("/comments/update")
    @SendTo("/topic/comments")
    public CommentResponse fixComment(
            @Valid @Payload UpdateCommentRequest updateCommentRequest,
            @AuthenticationPrincipal AccessTokenPayload payload) {
        Long userId = payload != null ? payload.userId() : null;
        CommentUpdateCommand command = commentCommandMapper.requestToUpdateCommand(updateCommentRequest, userId);
        CommentResult commentResult = commentCrudInputPort.updateComment(command);
        return commentResponseMapper.resultToResponse(commentResult);
    }

    @MessageMapping("/comments/delete")
    @SendTo("/topic/comments")
    public DeleteCommandRequest deleteComment(
            @Valid @Payload DeleteCommandRequest deleteCommandRequest,
            @AuthenticationPrincipal AccessTokenPayload payload) {
        Long userId = payload != null ? payload.userId() : null;
        boolean isAdmin = checkIsAdmin(userId);
        CommentDeleteCommand command = commentCommandMapper.requestToDeleteCommand(deleteCommandRequest, userId, isAdmin);
        commentCrudInputPort.deleteComment(command);
        return deleteCommandRequest;
    }

    private boolean checkIsAdmin(Long userId) {
        if (userId == null) return false;
        List<String> roleSet = roleRepositoryPort.findRoleNamesByUserId(userId);
        return roleSet.contains(RoleName.ADMIN.name()) || roleSet.contains(RoleName.CONTENT_MANAGER.name());
    }
}
